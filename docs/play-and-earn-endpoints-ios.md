# Play & Earn — endpoint inventory for the iOS integration

Everything the Android client (Aptoide Games, `:app-games`) calls to run Play & Earn (P&E), as of
2026-10-13. Compiled from the Android client (`aptoide-client-v8`, branch
`feature/AND-875_PlayAndEarnPhase1`) and the backend (`aptoide-incentivised-ads-services`,
FastAPI, `/api` root). Field names are the exact JSON keys on the wire.

Sections:

1. Hosts and environments
2. Identity: wallet JWT and `guest_id`
3. P&E API (rewards host) — what the app calls
4. P&E API — routes the app must NOT call (for context only)
5. Supporting endpoints outside the P&E backend
6. The developer's MMP click link (AppsFlyer)
7. End-to-end flows as Android implements them
8. Remote config flags
9. iOS-specific notes and open questions

---

## 1. Hosts and environments

| Purpose | Production | Dev |
|---|---|---|
| P&E backend ("rewards") | `https://rewards.aptoide.com/` | `https://rewards.dev.aptoide.com/` |
| Aptoide MMP (attribution / apkfy) | `https://aptoide-mmp.aptoide.com/api/v1/` | `https://aptoide-mmp.dev.aptoide.com/api/v1/` |
| Aptoide webservices v7 (Google sign-in → wallet) | `https://webservices.aptoide.com/api/7/` | `https://webservices-devel.aptoide.com/api/7/` |
| Catappult API chain (wallet balance, token refresh, gamification) | `https://apichain.catappult.io/` | `https://apichain.dev.catappult.io/` |
| BDS API chain (gamification levels, older path) | `https://apichain.blockchainds.com/` | same |

Every P&E backend route lives under `/api`. Swagger UI is served at `https://rewards.aptoide.com/api/docs`
(the root redirects there).

Dev caveat: `rewards.dev.aptoide.com` is behind a Cloudflare rule that 403s some office IPs on every
path. If dev returns 403 with no body, that is the WAF, not the API.

---

## 2. Identity: wallet JWT and `guest_id`

P&E users are identified by an **AppCoins wallet address**. There is no P&E-specific login.

### 2.1 Signed-in users: Bearer JWT

- Header: `Authorization: Bearer <auth_token>`.
- The token is an RS256 JWT issued by the Aptoide/AppCoins auth service. The backend verifies it with
  the public key named `wallet_blockchain` and reads the wallet address from the `sub` claim.
  `exp` is enforced (seconds or milliseconds both accepted); `iat` must not be in the future.
- Any invalid/expired token → `401 {"detail": "Invalid Signature"}`.
- Android caches the token for 1 hour (`UserWalletData.TTL_IN_SECONDS = 3600`) and refreshes it with the
  refresh token before each call once stale (see 5.2). Routes marked **auth optional** below are
  called without the header when the user is not signed in.

How the token is obtained (Android): Google Sign-In with *offline access* → a `serverAuthCode` →
`POST /user/authorize` on webservices (5.1) → returns `address`, `auth_token`, `refresh_token`.

### 2.2 Guests: `guest_id` query parameter

The app appends `?guest_id=<id>` to **every** P&E backend call (signed in or not). The backend uses it
for guest progress (missions, campaigns, sessions) and to link a guest's progress to the wallet after
sign-in.

- Android value: the `guest_uid` returned by the Aptoide MMP `/attribution` call (5.3), stored
  locally. Before attribution succeeds a locally generated 40-hex-char id is used. If that is empty
  the Firebase Messaging token is the fallback.
- Backend constraints: 1–255 characters. Required on `POST /api/events`, optional elsewhere.
- Android adds it with an OkHttp interceptor that gives up after 2 s and sends the request without
  it rather than stall.

### 2.3 Other headers

The P&E modules use the "raw" OkHttp client: only `User-Agent` and the two interceptors above.
`User-Agent` on Android is `aptoide-games/<version> ...`; the backend does not parse it. No
`Accept-Language`, no version-code header.

Client IP matters: the backend reads `X-Forwarded-For` (first entry) for GeoIP (country of the
session, timezone offset). Nothing to do client-side.

---

## 3. P&E API (rewards host) — what the app calls

Base: `https://rewards.aptoide.com/api`. All bodies are JSON. Validation failures return
`422 {"detail": [...]}` (FastAPI format). Unknown enum values from the server should be tolerated
(Android maps unknown mission status / attribution status / session status to "unknown" and carries
on).

### 3.1 `GET /api/client/config` — client configuration

Auth: none. Called once on app start (only if P&E is visible per remote config).

Response:

```json
{ "heartbeat_interval_seconds": 20, "redeem_units_amount": 500 }
```

- `heartbeat_interval_seconds`: how often to send session heartbeats. Server default 20. Android
  ignores values below its own minimum.
- `redeem_units_amount`: units needed for one redeem (one Roblox/Free Fire code). Android persists
  both values and uses them until the next fetch.

### 3.2 `GET /api/campaigns` — the P&E game feed

Auth optional. Query:

| Param | Type | Notes |
|---|---|---|
| `sections` | repeated | `trending`, `keep_playing`. Android sends both: `?sections=trending&sections=keep_playing`. |
| `limit` | int | default 10 |
| `guest_id` | string | see 2.2 |

Response (sections absent when not requested or empty; `keep_playing` only when the caller is
identified by wallet or `guest_id`):

```json
{
  "keep_playing": [ { ...CampaignItem } ],
  "trending":     [ { ...CampaignItem } ]
}
```

`CampaignItem`:

```json
{
  "app_info": { "package": "com.x.y", "icon": "https://…", "graphic": "https://…",
                "name": "Game", "uname": "game-store-uname" },
  "progress": { "current": 120, "target": 500, "type": "gxp", "status": "in_progress" },
  "total_prizes": 500
}
```

- `progress.type` is always `gxp` here (campaign-level total). `status` is `in_progress` / `completed`
  / null. `current` is null for anonymous callers.
- `app_info` fields may be empty strings when the app lookup failed server-side; handle that.
- `keep_playing` = campaigns with status `in_progress` for this user.

### 3.3 `GET /api/campaigns/{package_name}/missions` — missions of one game

Auth optional. Query: `guest_id`. This is the central call: app view, Rewards tab, session start,
status heartbeat refresh, and the MMP click link all read it.

Response:

```json
{
  "checkpoints": [ Mission ],
  "missions":    [ Mission ],
  "campaign_id": 42,
  "mmp_click_url": "https://app.appsflyer.com/com.x.y?pid=aptoide_int&c=…&clickid={transaction_id}&af_sub1=…",
  "attribution": { "status": "active", "reason": null, "confirmed_at": "2026-10-01T12:00:00" }
}
```

`Mission`:

```json
{
  "title": "Reach level 5",
  "description": "…",
  "icon": "https://…",
  "type": "EVENT",
  "arguments": { "event_name": "af_level_achieved" },
  "units": 100,
  "progress": { "current": 0, "target": 1, "type": "count", "status": "pending" }
}
```

- `type`: `PLAY_TIME` | `STREAK` | `CHECKPOINT` | `EVENT`.
- `progress.type` is derived from the mission type: `seconds` (PLAY_TIME), `count` (STREAK, EVENT),
  `gxp` (CHECKPOINT).
- `progress.status` is lowercase: `pending` | `in_progress` | `completed` | null. `current` is null
  when the caller is anonymous (no wallet, no guest_id).
- `arguments` is a free-form object. Known keys: `event_type` on the FIRST_SIGN_IN event mission
  (3.4), `event_name` on MMP event missions.
- `campaign_id` + `mmp_click_url`: only present when the game has a **live** campaign with a click
  link. Both are needed to fire the MMP click (section 6).
- `attribution`: **only for signed-in callers**, null otherwise. The developer's MMP verdict on this
  user's install:

| `status` | Meaning | Android behaviour |
|---|---|---|
| `not_started` | No MMP install postback yet | Show missions; "rewards pending" until confirmed |
| `active` | MMP credited the install to Aptoide | Normal |
| `not_eligible` | MMP rejected it (`reason`: `rejected_fraud`) | "Rewards paused" state; stop status heartbeats |
| `completed` | Every mission/checkpoint completed | Done |
| `expired` | Campaign ended | Hide / grey out |

Android caches the missions (minus `campaign_id` / `mmp_click_url` / `attribution`) in a local DB and
reads the cache unless `forceRefresh`.

### 3.4 `GET /api/missions` — global missions by type

Auth optional. Query:

| Param | Notes |
|---|---|
| `mission_type` | **required**, repeatable: `PLAY_TIME`, `STREAK`, `CHECKPOINT`, `EVENT`. Android sends `?mission_type=EVENT`. |
| `package_name` | optional filter |
| `guest_id` | see 2.2 |

Response: one array per requested type, keyed `play_time`, `streak`, `checkpoint`, `event`
(absent when empty). Items are `Mission` (3.3) plus `"package": "com.x.y"` (null for global,
campaign-less missions).

Android use: find the **first sign-in reward** mission — the item in `event` whose
`arguments.event_type == "FIRST_SIGN_IN"`. Its `units` is the reward, its `progress.status ==
"completed"` means already claimed.

### 3.5 Sessions — `POST /api/v1/sessions/*`

Auth **required** (wallet JWT). A session is "this wallet is playing this package on this device".
One active session per wallet; starting a new one closes the others. Session ownership lives in
Redis with a 180 s TTL, renewed by every accepted heartbeat.

#### `POST /api/v1/sessions/start`

Body: `{ "package": "com.x.y", "device_id": "<stable device id>" }`. Query: `guest_id` optional.

Response: `{ "session_id": "uuid", "stolen": false, "ttl": 180 }`

- `stolen: true` means another active session of this wallet was closed.
- Calling start again from the same device while a session is live just renews it and returns the
  same `session_id`.
- Errors: no live campaign for the package → domain error (treat non-2xx as "no session").
- Android `device_id` = Firebase Messaging token. The backend only compares it for equality, so iOS
  can use any stable per-install id (see 9).

#### `POST /api/v1/sessions/heartbeat`

Body:

```json
{
  "session_id": "uuid",
  "package": "com.x.y",
  "seq": 3,
  "seconds": 0,
  "device_id": "…",
  "device_ts_ms": 1760000000000,
  "tz_offset_min": 60
}
```

- `seq`: strictly increasing per session, starting at 0. Android increments it after **every**
  attempt (success or failure) so a retry never reuses a number.
- `seconds`: play time since the last heartbeat, 0–600; the server clamps to 60 per tick.
  **`seconds: 0` is the status heartbeat (AND-879)**: it counts no play time, keeps the session
  alive, and returns the queued `events`. That is the only kind iOS can send (see 9).
- `device_ts_ms` / `tz_offset_min`: device clock and UTC offset in minutes (used for streak-day
  boundaries when the GeoIP offset is unknown).

Response:

```json
{
  "status": "ok",
  "applied_seconds": 0,
  "ttl": 180,
  "events": [
    { "type": "MISSION_COMPLETED", "mission_title": "Reach level 5", "earned_units": 100, "package": "com.x.y" }
  ]
}
```

`status` values and what Android does:

| `status` | Android |
|---|---|
| `ok` | apply; process `events` |
| `duplicate_or_out_of_order` | bump `seq`, keep session |
| `session_expired`, `session_not_found`, `paused_by_other_device` | drop the local session; recreate on next tick |
| `ignored_zero` | legacy (pre-AND-879); treat as ok |

`events` is the list of pending user events (mission confirmations) the server has queued for this
wallet; the same event can appear in consecutive responses for a short replay window, so dedupe by
`mission_title` + `package`. Android matches each event to the session's missions, marks them
completed locally, and posts the "mission completed" notification.

#### `POST /api/v1/sessions/end`

Body: `{ "session_id": "uuid", "package": "com.x.y", "device_id": "…" }`

Response: `{ "status": "ended", "applied_seconds": 0, "missions_updated": 0, "lock_cleared": true }`.
`status` ∈ `ended` | `already_ended` | `not_found`. Idempotent. Android calls it when the status
heartbeat loop stops and when the game changes.

### 3.6 `POST /api/events` — submit a client-side event

Auth **required**. Query: `guest_id` **required**.

Body: `{ "event_type": "FIRST_SIGN_IN", "package_name": null }` (`package_name` optional; Android
omits it). `FIRST_SIGN_IN` is the only event type today.

Responses:

- `200 {"message": "Event submitted."}` — reward granted.
- `409 {"detail": "Event already submitted for this user."}` — already claimed; Android switches the
  UI to "claimed" silently.
- `404 {"detail": "Event not found."}` — no event mission for this type.
- `422` — unknown `event_type`.

Android use: the sign-in reward card. After a successful claim it invalidates the cached units
balance (5.4).

### 3.7 Exchange / redeem — `/api/exchange/*`

#### `GET /api/exchange/rate/{country_code}`

Auth: none. Android always asks for `US` and caches the answer for the process lifetime.

Response: `{ "country_code": "US", "rate": 1.0, "credits_amount": 100 }` — `rate` is money per unit
used to display the reward value ("$1.23"); `credits_amount = rate × 100`.

#### `POST /api/exchange/v2` — redeem units

Auth **required**. Body: `{ "email": "user@example.com", "redeem_type": "ROBLOX_ROBUX" }`.
`redeem_type` ∈ `ROBLOX_ROBUX` | `FREE_FIRE_DIAMOND`. The amount is fixed server-side
(`redeem_units_amount` from 3.1).

Response: `200 {"transaction_id": "…"}`. Failures carry the use-case status code and a `detail`
which can be a string **or** an object `{"message": "...", "status": "..."}` — Android registers a
custom deserializer for both shapes. Treat any non-2xx or missing `transaction_id` as failure and show
`detail.message` / `detail`.

#### `POST /api/exchange/` — deprecated

Returns `410`. Do not implement.

### 3.8 Utilities

- `GET /api/utils/countrycode` — GeoIP lookup of the caller's IP (city/country object). Not used by
  Android today.
- `GET /api/utils/timezone-offset` — `{ "offset_seconds_utc": 3600 }`. Not used by Android today.
- `GET /api/status` — `{ "status": "OK" }` health check.

---

## 4. P&E API — routes the app must NOT call

Listed so the iOS side does not wonder what they are when reading the Swagger.

| Prefix | Caller | Auth |
|---|---|---|
| `GET /api/postbacks/install`, `GET /api/postbacks/event` | The developer's MMP servers (AppsFlyer partner template). Server-to-server. | `?key=<shared secret>` |
| `/api/playAndEarn/apps/{package}/campaigns…` (create / list / get / update / approve / reject / delete), `POST /api/playAndEarn/campaigns/list` | Aptoide Connect (developer dashboard) | JWT signed with the `aptoide_connect` key |
| `/api/bo/*` (campaigns, missions, sessions, rewards, progress, redeems, exchange rate, user resets) | Internal backoffice | HTTP Basic |
| `/api/report/*` (engagement reports, CPA tiers, dashboards) | Internal | HTTP Basic |

---

## 5. Supporting endpoints outside the P&E backend

These are not P&E routes but the Android P&E feature cannot work without them.

### 5.1 `POST https://webservices.aptoide.com/api/7/user/authorize` — Google sign-in → wallet

Auth: none. Body (Android):

```json
{
  "credential": "<Google serverAuthCode>",
  "type": "GOOGLE",
  "supported": "WALLETJWT",
  "accepted": ["TOS", "PRIVACY", "DISTRIBUTION"],
  "domain": "<app package / bundle id>",
  "channel": "ANDROID",
  "consents": ["email"]
}
```

Response:

```json
{
  "type": "WALLETJWT",
  "state": null,
  "data": { "address": "0x…", "auth_token": "<JWT>", "refresh_token": "<JWT>", "email": "user@gmail.com" }
}
```

Android obtains `credential` through Google Identity `AuthorizationRequest` with
`requestOfflineAccess(GOOGLE_AUTH_CLIENT_ID)` (a **server auth code**, not an ID token). `domain` is
the app's package name. The wallet `address` is the P&E user id from here on.

### 5.2 `GET https://apichain.catappult.io/appc/user_wallet` — refresh the wallet JWT

Header: `Authorization: Bearer <refresh_token>`.
Response: `{ "address": "0x…", "auth_token": "<new JWT>", "refresh_token": "<new refresh token>" }`.
Store both; the refresh token rotates. Android refreshes lazily when the cached auth token is
older than 1 hour, under a mutex so concurrent calls share one refresh.

### 5.3 `GET https://aptoide-mmp.aptoide.com/api/v1/attribution` — first-open attribution (AND-856)

Auth: none. Query:

| Param | Android value |
|---|---|
| `package_name` | the app's own package |
| `guest_uid` | locally generated 40-hex id (sent until the server returns one) |
| `timestamp` | ms epoch of the **first** attempt (kept across retries) |
| `vercode` | app version code |
| `installer_package_name` | installer package, may be null |

Response: `{ "package_name", "oemid", "guest_uid", "utm_source", "utm_medium", "utm_campaign",
"utm_term", "utm_content" }` — all nullable strings.

Android calls it once per install on first open, retrying with exponential backoff for a minute and
then every minute until it succeeds, then never again. The returned `guest_uid` becomes the
permanent `guest_id` for P&E (2.2). Legacy: `GET …/apkfy` (same response shape, plus `app_id`)
still exists but is only used for analytics probing now.

### 5.4 `GET https://apichain.catappult.io/transaction/1.20230807/wallet/{address}/info?currency=USD` — balance

Auth: none (address in path). The P&E UI reads **`units_balance`** (integer) for the toolbar
badge, balance screen and redeem threshold. Other fields (`appc_balance`, `appc_c_balance`,
`eth_balance`, fiat equivalents, `blocked`, `verified`, …) are wallet-app concerns.

Android refreshes it after a redeem, after claiming the sign-in reward, and whenever a mission is
confirmed.

### 5.5 Gamification (level / bonus) — Catappult

- `GET https://apichain.catappult.io/gamification/1.20230531/user_stats?address=0x…&lang_code=en&currency=USD`
  → `{ "promotions": [ { "id": "GAMIFICATION", "level": 3, "bonus": 7.5, "status": "ACTIVE",
  "gamification_status": "STANDARD", "current_amount_currency": …, "next_level_amount_currency": …,
  … } ], "wallet_origin": "APTOIDE" }`. Android takes the promotion with `id == "GAMIFICATION"`.
- `GET https://apichain.catappult.io/gamification/levels?address=&currency=USD`
  → `{ "result": [ { "level": 1, "amount_currency": …, "bonus": 5.0 } ], "status": "ACTIVE",
  "update_date": "…" }`.

Used only by the "Level up" screen (current level, bonus %, next-level thresholds). Optional for a
first iOS cut.

---

## 6. The developer's MMP click link (AppsFlyer)

Campaign attribution is done by the **developer's** MMP, not by an SDK in our app. When the user
taps Install on a P&E game, the app fires one HTTPS GET to the campaign's `mmp_click_url` (from 3.3)
so AppsFlyer credits the install to Aptoide and later calls our postbacks with the user's wallet.

Android (`PaEMmpClickUrlBuilder`, AND-877) fills the template like this:

| Placeholder in template | Value |
|---|---|
| `{transaction_id}` | a fresh UUID (our click id) |
| `{google_aid}` | Google Advertising ID; the whole query param is dropped when missing/zeros |
| `{affiliate_id}` | channel, `aptoide_games` |
| `{offer_id}`, `{offer_ref_id}` | `campaign_id` |
| `af_sub1` (added/overwritten) | the user's wallet address, URL-encoded |

Rules: template must start with `https://`; placeholders are only substituted inside query values;
any unfilled `{…}` left anywhere → do not send. The request is fire-and-forget (response ignored,
failures logged). It is skipped when the user is not signed in (no wallet → postback can't be linked).
The link is re-fetched from `/missions` at click time, never from cache.

After install, the outcome shows up as `attribution.status` in `/missions` and as `events` in the
heartbeat response.

---

## 7. End-to-end flows as Android implements them

1. **App start**: `/attribution` (once per install, 5.3) → `guest_id`. If P&E is visible (section 8):
   `GET /api/client/config`.
2. **Home / P&E hub**: `GET /api/campaigns?sections=trending&sections=keep_playing`.
3. **Sign in** (required to earn): Google → `POST /user/authorize` → wallet + tokens. Then the
   sign-in reward: `GET /api/missions?mission_type=EVENT` → show the FIRST_SIGN_IN offer → on claim
   `POST /api/events {"event_type":"FIRST_SIGN_IN"}` → refresh balance (5.4).
4. **Game page / Rewards tab**: `GET /api/campaigns/{package}/missions`.
5. **Install**: fire the MMP click (section 6).
6. **Play** (tap Play, game goes to foreground):
   - `POST /api/v1/sessions/start` → `session_id`, `ttl`.
   - `GET /api/campaigns/{package}/missions` (fresh) → missions for this session.
   - Loop every `heartbeat_interval_seconds` (20 s): `POST /api/v1/sessions/heartbeat` with
     `seconds: 0` → process `events` (mission confirmed → local notification, mark completed, refresh
     balance). Every 5 min also re-fetch `/missions` to pick up the attribution status.
   - Stop when: every non-time-based mission is completed/confirmed, `attribution.status ==
     not_eligible`, the screen has been off for 10 min, or a backstop (default 10 h, remote config)
     passes. Three consecutive failures to (re)create the session also stop it. Then
     `POST /api/v1/sessions/end`.
   - With usage tracking on (Android only, flag off in prod), the same loop sends real `seconds`
     for PLAY_TIME missions.
7. **Balance & redeem**: `GET /wallet/{address}/info` → `units_balance`; `GET /api/exchange/rate/US`
   for display; `POST /api/exchange/v2` when `units_balance >= redeem_units_amount`.

---

## 8. Remote config flags (Firebase on Android)

| Key | Type | Meaning |
|---|---|---|
| `show_play_and_earn` | bool | P&E visible at all (geo-targeted in Firebase too) |
| `pae_countries` | JSON array of ISO-3166 alpha-2 | locally detected country must be in it; default `US CA FI FR DE IT NL NO PT ES SE GB` |
| `pae_usage_tracking_enabled` | bool | time-based missions + usage permissions. **Off**; irrelevant for iOS |
| `pae_status_heartbeat_max_hours` | int 1–24 | backstop for the post-Play status heartbeat loop (default 10) |

P&E is also hidden on rooted/compromised devices.

---

## 9. iOS-specific notes and open questions

Facts that follow from the code, then things to agree with backend/product before building.

**What iOS can and cannot do**

- iOS has no foreground-app usage access, so **only the status heartbeat (`seconds: 0`) applies**.
  PLAY_TIME and CHECKPOINT missions cannot progress from iOS; EVENT missions (confirmed by the
  developer's MMP postbacks) and the FIRST_SIGN_IN reward can. That is the same situation as
  Android in production today, where usage tracking is flag-off.
- "While the game is running" has to be approximated on iOS too. Android's policy (section 7, step
  6) is a reasonable template, with iOS background-execution limits in mind: a short loop while our
  app is foregrounded plus a refresh of `/missions` on return is probably the realistic shape.
- Mission confirmations arrive only via heartbeat `events` or by re-reading `/missions`. There is no
  push.

**Parameters whose Android values are Android-specific**

| Where | Android value | iOS suggestion / question |
|---|---|---|
| `device_id` (sessions) | FCM token | any stable per-install UUID; the backend only compares it for equality to decide who owns the session |
| `guest_id` fallback | Firebase token | not needed if `/attribution` is called first; confirm the Aptoide MMP `/attribution` route accepts an iOS bundle id and `installer_package_name` omitted |
| `user/authorize.channel` | `"ANDROID"` | ask the auth team whether an `"IOS"` value exists or `"ANDROID"` is accepted |
| `user/authorize.domain` | app package name | bundle id? needs the auth team to whitelist it |
| MMP click `{google_aid}` | GAID | IDFA, or drop the param (Android drops it when unavailable, so that is safe) |
| MMP click `{affiliate_id}` | `aptoide_games` | agree a channel value with backend; it is informational today |
| `User-Agent` | `aptoide-games/…` | free |

**Behaviours to replicate**

- Strictly increasing `seq` per session; increment on failure too.
- Tolerate unknown enum values (new statuses are added without versioning).
- `/api/events` 409 = already claimed, treat as success for UI state.
- `/exchange/v2` error `detail` can be string or object.
- `guest_id` on every P&E call, also when signed in (links guest progress to the wallet).
- Wallet JWT refresh on the hour and on 401.

**Not yet defined anywhere (ask before relying on it)**

- Whether `/attribution` on the Aptoide MMP has an iOS flavour (it was built for APK installs).
- The developer side: AppsFlyer postbacks for iOS apps send the bundle id, and the backend already
  strips a trailing `-<channel>` from AppsFlyer's app id, but no iOS campaign has been run end to end.
- Redeem types are Roblox/Free Fire codes delivered by email; nothing iOS-specific, but App Store
  policy on real-money-equivalent rewards is a product question.

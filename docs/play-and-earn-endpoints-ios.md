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

### 3.8 `GET /api/v1/account/status` — is the account restricted? (AND-951)

Auth **required** (Bearer wallet JWT, like the other P&E calls). Signed-in users only: never called
for a guest.

Response: `{ "restricted": true }` or `{ "restricted": false }`. `true` means the backend banned the
account for fraud; every other P&E call of that account is refused with `ACCOUNT_RESTRICTED` anyway.

It is a convenience, not a control: on any failure (network, `5xx`, `401`, a body without a boolean
`restricted`) Android knows nothing more than before: it keeps what it last knew (restricted or not),
treats a first failure as not restricted, may try again after 10 s, and reports nothing.

**The restricted screen.** Android asks this when a P&E screen opens, at most once a minute (an
answer of `false` lifts a restriction, so an account support has un-banned gets back in without
reinstalling); while the restricted screen is shown it also asks when the app comes back to the
front and once a minute. It counts the account as restricted as soon as any P&E call is refused
with `ACCOUNT_RESTRICTED` (session start, heartbeat, redeem, sign-in bonus, balance, exchange rate),
and then asks this at once: the answer about the current account wins, and a failure leaves the
refusal standing. The answer is kept in memory only and forgotten on sign-in and sign-out. While a
signed-in account is restricted, every P&E screen (the Rewards screen and home tab, the info
screens, the permissions screen, the redeem flow, a game's Rewards tab, the apkfy reward screens)
shows "Account restricted — Your account has been restricted due to anti-fraud mechanisms. Contact
support in Settings." with a Contact Support button (the support e-mail form, with the subject
"Account restricted" and the wallet address added after the device details), and the home sign-in
bonus card is hidden. Until the first answer arrives the screens show their normal content, except
the apkfy reward offers, which wait for it (they claim the bonus on their own after a sign-in).

### 3.9 Installs — `POST /api/v1/installs/*` (AND-954)

Auth **required** (Bearer wallet JWT, like the other P&E calls). Signed-in users only, and only
while P&E is available to the user (the same check that shows P&E at all: region, device, remote
flag, section 8); while it is not, nothing is sent and pending installs wait. The backend counts
the day's installs with them (20 a day per account or phone) and tells a first install from
another account installing the same game on the same phone. Both are new routes: they must be
registered in Cloudflare API Shield before an app can reach them.

**Phone headers.** The backend reads the phone headers (`X-PaE-Device-Key`…) on these calls like on
the other P&E calls. On Android they are added to every P&E call by AND-949, which is a separate
change: these two calls need no code of their own for them. Without the headers the backend counts
the limit per account only and can never find a prior install, so the two must ship together.

**Rule: the click before the report.** A report whose click is not on record is not stored, only
flagged. The click is sent when the install starts; when it never reached the backend (offline,
`5xx`), the app sends the same click again before the report (a repeated `click_id` changes
nothing).

#### `POST /api/v1/installs/click`

The install of a P&E game started. Body: `{ "package": "com.x.y", "click_id": "<uuid>" }`.

- `click_id`: the app's own id for this install (letters, digits and `. _ : -`, at most 128): the
  **same value** the MMP click link carries (section 6). Sent for every P&E install, whether the
  campaign has an MMP link or not, never for an update of a game already on the phone.
- It never blocks or delays the download, whatever the answer.

| Answer | What Android does |
|---|---|
| `200 {"eligible": true}` | Nothing |
| `200 {"eligible": false}` | Nothing: the game's missions already come back `not_eligible` (3.3) |
| `429` `INSTALL_LIMIT` with `retry_at` | One long snackbar: "You've reached today's Play & Earn install limit. Your game will still download. The limit resets on {retry_at as a local date and time}." (without the last sentence when `retry_at` is missing or past). The install goes on; it is never reported, and the same game started again within 10 minutes is not clicked again |
| `403` `ACCOUNT_RESTRICTED` | The restricted screen (3.8); never reported |
| `422` | A bug: never sent again, never reported |
| `503` / network | Sent again before the report |

#### `POST /api/v1/installs/report`

The install finished. Body:

```json
{
  "package": "com.x.y",
  "click_id": "<the click's id>",
  "installer": "com.aptoide.android.aptoidegames",
  "signature": "<64 hex characters>"
}
```

- `installer` (optional): the package the phone names as the game's installer.
- `signature` (optional): SHA-256 of the game's current signing certificate, 64 lower-case hex
  characters. Either is left out when it cannot be read.
- Answers: `200`, `403` (restricted), `422`, `503`. A `403` or `422` is not sent again; anything
  else is tried again at the next app start.

**How Android sends them.** The click goes out when an install of a P&E game starts, from any
screen: the install manager queuing it or Google Play taking it (inline installs). From the P&E
install views the MMP click goes out at the same time with the same id. The install's package,
click id, start time and owner (the wallet that started it) are kept on disk, one per game: the
same game started again by the same account keeps its click id. The report goes out as soon as the
game is installed (whatever screen is open), or at the next app start for an install that finished
while the app was not running, and only for the account that started it: another account's
pending install is dropped, never reported or clicked again. A pending install is dropped after 7
days, a refused one after 10 minutes, all of them on sign-in or sign-out, and the oldest beyond
200; it is never reported while the game is not on the phone.

### 3.10 Utilities

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
| `{transaction_id}` | the install's click id, a UUID: the same one `POST /api/v1/installs/click` carries (3.9) |
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
5. **Install**: `POST /api/v1/installs/click` (3.9) and, with the same click id, the MMP click
   (section 6). When the game is installed: `POST /api/v1/installs/report`, its click first.
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
8. **Any P&E screen, signed in**: `GET /api/v1/account/status` (at most once a minute, 3.8) → the
   restricted screen instead of the screen while `restricted` is `true`.

---

## 8. Remote config flags (Firebase on Android)

| Key | Type | Meaning |
|---|---|---|
| `show_play_and_earn` | bool | P&E visible at all (geo-targeted in Firebase too) |
| `pae_countries` | JSON array of ISO-3166 alpha-2 | locally detected country must be in it; default `US CA FI FR DE IT NL NO PT ES SE GB` |
| `pae_usage_tracking_enabled` | bool | time-based missions + usage permissions. **Off**; irrelevant for iOS |
| `pae_status_heartbeat_max_hours` | int 1–24 | backstop for the post-Play status heartbeat loop (default 10) |
| `pae_device_checks` | JSON array of device check names, e.g. `["su_binary","root_apps"]` | which device checks run; a check left out does not run. Default (flag absent or unparseable): all checks. Unknown names are ignored, and a list with **no** known name (including `[]`) is treated as invalid: all checks run, so the flag can silence single checks but never all of them |

P&E is also hidden on compromised devices: when one of the emulator checks (`emulator_build`, `emulator_props`, `emulator_files`, `unknown_board`) or root checks (`root_files`, `su_binary`, `root_apps`, `dangerous_props`, `rw_system_paths`, `test_keys`) fires, on release builds. The runtime checks (`xposed`, `hook_libraries`, `debugger`, `test_harness`, `app_debuggable`) never hide P&E; they are only reported. The names of the checks that fired go to Firebase once per process as `pae_device_risk` (`checks`: sorted, comma-separated names, continued in `checks_2`, … past 100 characters; `count`: how many fired).

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

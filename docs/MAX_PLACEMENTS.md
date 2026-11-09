# MAX placements: native and MREC

How the AppLovin MAX placements in `:app-games` are wired, gated, measured and switched on.

Serving state at a glance:

- **Search defaults to MREC** in Games and Vanilla when that brand's MREC unit is configured.
  The shipped selector defaults are enabled with a 100% MREC allocation, and the MREC placement
  defaults enabled in both build types. An explicit Remote Config value still overrides these
  defaults. Missing MREC ids select the native control before any ad request.
- **Browse placements default on** in release and debug for both Games and Vanilla: categories,
  Top Charts and ordinary See All lists. The deeper Home slot also defaults on. Explicit remote
  values still override these defaults; geo, configured-unit and layout gates still apply. Existing
  Home and detail flags, RTB exclusions and Google Play ad exclusion are unchanged.
- **Auto-refresh remains off** in the app and both MAX MREC units. No public app release, CI
  properties publication or Firebase change has been performed. The two MREC ids are wired in
  existing local Gradle settings; CI needs the matching brand properties before shipping.

## 1. Placements

| Placement (`NativeAdPlacement`) | Surface | Flag prefix | MAX placement name | Unit id field | Default position |
|---|---|---|---|---|---|
| `HOME_BUNDLE` | Home feed, compact card before the 3rd bundle | `home_native` | `home_bundle` | `HOME_NATIVE_AD_UNIT_ID` | 2 |
| `HOME_DEEP` | Home feed, deeper slot (enabled by default) | `home_native_deep` | `home_bundle_deep` | `HOME_NATIVE_AD_UNIT_ID` | 6, min gap 3 |
| `SEARCH_LANDING` | Search landing, native arm | `search_native` | `search_landing` | `SEARCH_NATIVE_AD_UNIT_ID` | 0 |
| `SEARCH_MREC` | Search landing, MREC arm (300x250) | `search_mrec` | `search_landing_mrec` | `SEARCH_MREC_AD_UNIT_ID` | 0 |
| `APP_DETAIL` | App page, between screenshots and What's New | `appview_native` | `app_detail` | `APP_DETAIL_NATIVE_AD_UNIT_ID` | 0 |
| `APP_DETAIL_BOTTOM` | App page, after the description (min content length) | `appview_native_bottom` | `app_detail_bottom` | `APP_DETAIL_NATIVE_AD_UNIT_ID` | 0 |
| `CATEGORY_LIST` | Category app list, after the 3rd row | `browse_native_category` | `category_list` | `BROWSE_NATIVE_AD_UNIT_ID` | 3 |
| `TOP_CHARTS` | Top Charts, unranked card after the 3rd row | `browse_native_top_charts` | `top_charts` | `BROWSE_NATIVE_AD_UNIT_ID` | 3 |
| `COLLECTION_LIST` | Ordinary (non-RTB) See All lists | `browse_native_collection` | `collection_list` | `BROWSE_NATIVE_AD_UNIT_ID` | 3 |

Never served: RTB lists and carousels, Updates, My Games, bonus and Play and Earn surfaces, the
install sheets, GameGenie answers. `AdExclusionGuardTest` scans the sources for this.

## 2. Build-time dependencies (per brand, blank-safe)

Unit ids and the AdMob app id come from Gradle properties (or environment variables of the same
name), read in `app-games/build.gradle.kts` by `configureMaxAdUnits`. A missing property is an
**empty string**, and an empty unit id is a permanent `no_ad_unit_id` skip: nothing is requested,
no SDK is initialised for it.

| Brand flavor | Property prefix | Properties |
|---|---|---|
| `aptoideGames` | (none) | `HOME_NATIVE_AD_UNIT_ID`, `SEARCH_NATIVE_AD_UNIT_ID`, `APP_DETAIL_NATIVE_AD_UNIT_ID`, `BROWSE_NATIVE_AD_UNIT_ID`, `SEARCH_MREC_AD_UNIT_ID`, `AD_MOB_APPLICATION_ID` |
| `vanilla` | `VANILLA_` | `VANILLA_HOME_NATIVE_AD_UNIT_ID`, `VANILLA_SEARCH_NATIVE_AD_UNIT_ID`, `VANILLA_APP_DETAIL_NATIVE_AD_UNIT_ID`, `VANILLA_BROWSE_NATIVE_AD_UNIT_ID`, `VANILLA_SEARCH_MREC_AD_UNIT_ID`, `VANILLA_AD_MOB_APPLICATION_ID` |

Both brands share `APPLOVIN_SDK_KEY`. Rules:

- Vanilla (`cm.aptoide.pt`) never reads the Games properties. MAX already holds Vanilla native
  units for Home, Search and app detail (Native / Manual, under the `cm.aptoide.pt` application);
  they only need **wiring** into the `VANILLA_HOME_NATIVE_AD_UNIT_ID`,
  `VANILLA_SEARCH_NATIVE_AD_UNIT_ID` and `VANILLA_APP_DETAIL_NATIVE_AD_UNIT_ID` properties, not
  re-creation. The Vanilla Browse property remains blank locally. Its Search MREC property
  now points to its own dedicated Vanilla MREC unit; CI still needs the matching brand keys.
- `SEARCH_MREC_AD_UNIT_ID` must be a **dedicated MREC-format unit**. A native unit id in that
  property will not fill and must never be used.
- The Games unit [AptoideGames_Android_MREC_SearchLanding](https://max.applovin.com/o/mediation/ad_units/edit/205981/11323522)
  was created on 8 November 2026 and filled with an AppLovin test creative on the Samsung phone.
  Its dashboard settings are **No refresh**, bid floor off and frequency capping off. AppLovin
  and AppLovin Exchange supplied the initial demand. Mintegral, Liftoff and Unity bidding
  placements have since been created and their MAX mappings saved and independently reopened.
  The id is wired into existing local Gradle settings; CI still needs `SEARCH_MREC_AD_UNIT_ID`.
  Live bidder participation after the network changes remains unverified.
- The Vanilla unit [Vanilla_Android_MREC_SearchLanding](https://max.applovin.com/o/mediation/ad_units/edit/205981/11323888)
  was subsequently created under `cm.aptoide.pt`, with No refresh, bid floor off and frequency
  capping off. Its id is wired locally into `VANILLA_SEARCH_MREC_AD_UNIT_ID`. AppLovin/MAX
  supplied the initial demand. Mintegral and Unity bidding placements and MAX mappings are
  now saved. Vanilla Liftoff remains disabled: its package is registered elsewhere in Liftoff
  and the owner is resolving the registration. Both own-brand MREC units previously filled
  with AppLovin test creatives on the Samsung phone using normal Search defaults (no format
  or MREC-id override), with no parallel Search native request. More recent live checks
  returned no-fill before the network expansion; live fill must be retested.
- `AD_MOB_APPLICATION_ID` falls back to Google's published test app id when unset, so a build
  without the property still starts; set the real id before any release that serves ads.
- Production values live GPG-encrypted in the CI properties repository
  (`Aptoide/vanilla-gradle-properties`); the new keys above must be added there and the Jenkins
  "Update gradle.properties" job run before CI builds carry them. Only `distribution=direct` builds
  compile the ads code path (`NATIVE_ADS_ENABLED`); `gplay` builds never request or initialise ads
  and emit no ad analytics.

## 3. Remote Config keys

Per placement (`<prefix>` from the table above). A value set in the Firebase console always
wins; the "when absent" columns are fallbacks used only while a key is missing.

| Key | When absent, release | When absent, debug | Notes |
|---|---|---|---|
| `<prefix>_enabled` | true for `search_mrec`, `home_native_deep` and the three `browse_native_*` placements; false otherwise | true | Kill switch. Deliberately not in `remote_config_defaults.xml`. |
| `<prefix>_position` | per table (shipped in the XML) | same | List slots: organic rows before the ad. Home: bundle index. |
| `<prefix>_excluded_geos` | consent geos (`AdsDefaults.EXCLUDED_GEOS`) | empty | **Unknown country is ineligible whenever a blocklist is active.** |
| `<prefix>_min_content_length` | 0 in code; the XML ships `appview_native_bottom_min_content_length = 1500` | same | Only `appview_native_bottom` uses it. |
| `<prefix>_min_gap` | 3 (the XML ships `home_native_deep_min_gap = 3`) | 3 | Used by `home_native_deep`: bundles between the two home ads. |

Search format configuration (MREC is the shipped default; explicit remote values still win):

| Key | Default | Meaning |
|---|---|---|
| `search_ad_format_experiment_enabled` | `true` | Master switch. Off means the native control for everyone. |
| `search_mrec_allocation` | `100` | Percent of installations in the MREC arm (0..100, clamped). |
| `search_mrec_auto_refresh` | `false` | Let MAX refresh a loaded MREC while it is on screen and the host is resumed. |

At 100% allocation, MREC needs no installation-id lookup. Only a fractional allocation uses a
salted hash of the Firebase installation id (fallback: guest uid) modulo 100; without a stable
id, fractional allocation selects the native control. Raising a fractional allocation only adds
installations to the MREC arm. A missing MREC unit, a disabled format switch, or zero allocation
also selects native before requesting. At most one format is chosen per Search landing instance.
Once chosen, a slot that fails its placement flag, geo or fill gate shows nothing and never
requests the other format. In particular, `search_mrec_enabled = false` suppresses MREC;
`search_ad_format_experiment_enabled = false` is the switch back to native.

## 4. Activation steps for the new placements and Search experiment

1. Wire the ids into the CI properties repository under the names in section 2: the existing
   Games and Vanilla native units for Home, Search and app detail. For Browse, choose an
   appropriate existing same-brand Native unit or create a dedicated Browse Native unit for
   independent serving control. For Search MREC, use the existing Games and Vanilla
   **MREC-format** units above. Wire each brand's matching property in CI. Never reuse a native
   unit as the MREC id, and never point a Vanilla property at a Games unit.
2. Run the Jenkins properties job; confirm the next CI build logs no `no_ad_unit_id` skips for
   the intended placements (debug builds log `Native ad <PLACEMENT> not requested: <reason>`).
3. Verify on a test device with MAX test mode (Mediation Debugger) that each surface renders
   test creatives, and that gplay builds show nothing. Before requesting ads, confirm the SDK
   logs `Test Mode On: true`; passing test-device ids alone is not proof. If disabled, enable
   test mode in the debugger and relaunch the app as it requests. Games MREC has been verified
   with AppLovin test demand in both brands. Live rendering and bidder participation after
   the new network mappings remain unverified; a saved mapping is not proof of fill.
4. Browse, Search MREC and deeper Home default on without a remote enabled flag. Check for
   existing explicit `false` values, which still disable those placements. Other placements need
   `<prefix>_enabled = true` in Firebase to open in release. Keep each `<prefix>_excluded_geos`
   list aligned with the intended serving countries and consent configuration.
5. Search now ships with MREC enabled and a 100% allocation when its unit is configured.
   Existing explicit Firebase values can override that: check `search_mrec_enabled`,
   `search_ad_format_experiment_enabled` and `search_mrec_allocation` before release. A partial
   native/MREC comparison can still be configured through the allocation. Keep
   `search_mrec_auto_refresh = false` for the current inline-feed setup.
6. Roll back any placement with its `<prefix>_enabled` flag; roll back the experiment with
   `search_ad_format_experiment_enabled = false`. See the rollback timing below: neither takes
   effect instantly on devices that already hold a loaded ad or an open Search landing.

### Rollback timing

- Remote Config values reach the app on its own fetch schedule; the flags are read when a slot
  evaluates its gates, not continuously.
- A placement that already **holds a loaded ad keeps showing it** until that screen's ViewModel
  is cleared or the ad expires; the flag is re-checked on the next request (next screen instance,
  expiry reload, or a re-evaluation after a skip). Turning a flag off stops new requests, it does
  not pull ads that are already on screen.
- The **Search format decision is computed once per Search landing ViewModel** and cached. A
  landing that is open when the experiment is switched off keeps its arm until it is left; new
  landings (after the fetched config is applied) get the native control. So "every installation
  returns to native" happens over the following sessions, not immediately.

### Remaining release prerequisites

- Publish the intended same-brand Native, Browse and MREC properties through the CI properties
  job, and inspect the actual release artifact. Empty ids suppress requests even when a flag
  defaults on. The locally installed previews are debug-signed and must not be distributed.
- Browse, deeper Home and Search MREC default on, but explicit remote `false` still wins. Release
  geo restrictions are preserved; debug previews are not evidence of release eligibility in PT.
- Unity reporting is connected to MAX. Its developer website/app-ads.txt discovery and payout
  profile still need account-owner follow-up. Vanilla's sample AdMob app-id fallback and pending
  AdMob approval must be resolved before enabling that demand in a public build.
- Check the final release builds on both brands, including live no-fill behavior, installs and
  navigation. Google Play must remain free of this ad-serving path. Keep revenue and impressions
  separate from load/render counts, and monitor installs, retention, crashes and ANRs during rollout.
- Repository lint currently reports existing failures outside these ad changes. Baseline or fix
  those through the team's normal process before treating the release gate as green.

## 5. Lifecycle guarantees (unit tested in `app-games/src/test/.../ads`)

- Gates run in order on every attempt: distribution, unit id, Remote Config flag, content length,
  list length / insertion location, geo (unknown is blocked with a blocklist), SDK key, SDK init
  (15 s timeout). Nothing is latched before a gate; flag and geo skips are re-evaluated on the
  next activation at most once per minute.
- Transient failures (no fill, SDK init, exceptions) retry with exponential backoff
  2 s, 4 s, 8 s, 16 s, then rest for a minute. Retries run only while the slot is active (near
  the viewport and host resumed); toggling activity never skips a pending backoff.
- A slot is active only while it is within the live visible range of its own list and the screen
  is resumed. Scrolling far away or backgrounding stops retries, reloads and MREC refresh.
- Expired ads are destroyed and reloaded only when active; late or stale responses after release
  or replacement are destroyed and never shown. Each loaded ad is destroyed before replacement.
- The MREC view is owned by the Search landing ViewModel, so it survives typing, rotation and
  navigation like the native arm, and is destroyed with it. A failed refresh keeps the shown ad.
- Browse insertions keep organic indices, ranks and analytics positions; keys stay unique.

## 6. Analytics

Events are `<prefix>_<action>` with `format` (`native` or `mrec`), `version_code`, and `variant`
on Search (`native_control` / `mrec`): `skipped(reason, geo)`, `requested(geo, attempt)`,
`loaded(geo, network)`, `failed(geo, error_code, attempt)`, `expired`, `rendered` (assets bound
into a view, **not** viewability), `impression(geo, network, ecpm)` from the MAX revenue
callback, `clicked`. Builds without ads (`gplay`) emit none of them; the experiment bucket id is
never sent.

## 7. Local testing without production ids

When the Remote Config keys are missing, Search MREC defaults enabled in both build types.
Deep Home also defaults enabled in both build types; other placements default enabled in debug.
Debug uses an empty geo blocklist. Values set in the console still win. The source XML defaults select MREC at 100%.
Pass the ids on the command line only (never commit them), for example:

```bash
./gradlew :app-games:installAptoideGamesDirectDevDebug -PHOME_NATIVE_AD_UNIT_ID=<test unit> -PAPPLOVIN_SDK_KEY=<key>
```

An appropriate existing same-brand Native unit may supply `BROWSE_NATIVE_AD_UNIT_ID`: the
three placement names still distinguish where ads are shown. A separate Browse unit is
recommended when its waterfall, floor or serving settings need independent control. Sharing
a unit does not share an ad object: each placement owns its own load and lifecycle. The QA
Home-to-Browse override was command-line-only and must not be mistaken for saved production
configuration. A native unit may never stand in for `SEARCH_MREC_AD_UNIT_ID`.

## 8. Why auto-refresh is off

The original comparison kept one ad opportunity per Search landing. AppLovin additionally
[recommends stopping auto-refresh for MRECs inside content feeds](https://support.applovin.com/en/max/android/ad-formats/banner-and-mrec-ads),
reusing the view and refreshing manually. Search embeds this ad in scrollable content. This is
SDK guidance, not a format restriction: a separately approved refresh experiment can use the
existing timer controls, with additional visibility/refresh QA. Both current units use No refresh.

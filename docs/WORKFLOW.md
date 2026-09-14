# Task Workflow (SOP)

How every task is executed on this repo — by the humans **and** by Claude Code. The goal: aligned scope, clean architecture, tests that drive the code, independent review, and a clean PR. Follow it on every non-trivial task.

> Claude keeps an operating mirror of this in its memory; this file is the canonical, team-visible version. Keep the two in sync. The same SOP runs on aptoide-appstore (`services/docs/WORKFLOW.md`); this file adapts it to the Android client.

## 0. Recognise the task

A **task** is a distinct new feature, behaviour, or deliverable — normally one Linear ticket. When one begins, say so explicitly and enter the flow.

- **Detection is Claude's job — the trigger is intent, not a command.** Any request to build or change behaviour beyond a trivial one-liner starts the flow, even mid-conversation, and even when the scope feels already aligned from informal chat: informal alignment is NOT step 1. The **first action** of a task is entering plan mode (§1); writing code first is a workflow breach. The task **ends at the self-score (§7)**, not at the PR.
- **Escape hatch:** trivial one-liners (a typo, rename, config tweak, comment fix) skip the ceremony — call it out and just do it.
- **Scope-guard:** if, mid-build, the work outgrows the aligned plan/ticket, **stop and re-align** — update the plan or cut a new ticket. Never silently expand scope.

## 1. Align — plan mode + `/grill-me`

Before any code, converge on:

- **Scope** — what's in, what's explicitly deferred.
- **Architecture** — modules, boundaries, where the code lives.
- **Public/private interfaces & APIs** — the contract surface and the internal ports.
- **Key decisions** — every non-obvious one gets recorded (plan file / ticket / PR body).

Use `/grill-me` to stress-test until there's genuine shared understanding. Leave plan mode only on explicit approval (`ExitPlanMode`).

## 2. Linear coverage

Every piece of work maps to a **Linear** ticket, identified as an **Android client** ticket. (Jira and the `[AND-XXX]` prefix are temporarily not in use.)

- Missing coverage → create **right-sized** tickets or subtasks. Avoid giant catch-all tickets *and* avoid spamming tiny ones.
- Move the ticket to **In Progress** at the start; update it with the PR link and any follow-ups at the end.

## 3. Feature branch

`dev-v10` → **feature branch** (named from the ticket's `gitBranchName`) → work → **PR against `dev-v10`**. Release branches (`release-ag_*`) cherry-pick from `dev-v10`. Never commit directly to `dev-v10` or `master`.

When the work depends on an unmerged PR, **stack** the branch on that PR's branch. Ticket IDs in every commit message. Commits end with the `Co-Authored-By: Claude …` trailer — **no** "Claude-Session:" links and **no** "Generated with Claude Code" footers, on commits or PR bodies.

## 4. Build — Clean Architecture + TDD

**Architecture (this repo's conventions — see CLAUDE.md).** Multi-module Clean Architecture + MVVM: feature modules split `data / di / domain / presentation`; domain logic in feature modules, product-specific UI in product modules; brand divergence via per-source-set files (`src/vanilla/`, `src/aptoideGames/`, `src/gplay/`), not `BuildConfig` branches; theming through `Palette` / `FixedColors` tokens only.

**TDD (Kent Beck / Martin Fowler).** Per unit of behaviour:

1. **Write the test first.**
2. **Run it — watch it fail (red).** Confirm it fails for the right reason.
3. **Write the minimal code to pass (green).**
4. **Refactor** with the tests green.

Commit at green points. Tests are the executable spec — JUnit 5, Turbine for Flows, coroutines-test for suspend code; shared test deps come from the `:test` module via the `tests` convention plugin.

## 5. Pre-PR: gate, then three review agents

**First, the gate must be green:**

- Unit tests for every touched module (`./gradlew :module:test`, or the variant-specific task, e.g. `:app-games:testAptoideGamesGplayDevDebugUnitTest`).
- `./gradlew lint`.
- **On-device verification of both brand flavors** for any change in `:app-games/src/main/` (per CLAUDE.md — install both, screenshot the affected surface side-by-side).

The same gate runs in AptoideCI on every PR — run it locally first; CI is the backstop, not the discovery mechanism.

**Then fire three review agents in parallel**, each independent, each a distinct lens:

| Agent | Looks for |
| --- | --- |
| **Security** | injection, authz/authn, secrets, unsafe deserialization, data exposure |
| **Architecture** | module-boundary/dependency violations, source-set discipline, coupling, misplaced logic, hardcoded colors/strings |
| **QA / testing** | coverage gaps, missing edge cases, weak assertions, untested failure modes |

Each reports findings with file:line and severity. **Adversarially verify** each finding to filter false positives before acting (a finding survives only if it holds up under a skeptical second look); fix the real ones; re-run the gate.

## 6. Open the PR against `dev-v10`

Ticket-tagged title; body links the ticket, summarises the review outcomes, and states how it was verified (including flavor screenshots for UI changes).

## 7. Self-evaluate (1–10)

At the end of the task, score it honestly and **self-critically** — name the real weaknesses, not a comfortable number. Score two things:

- **Result quality** — is the code clean, tested, verified, maintainable?
- **Fit to Aptoide + the requirements** — architecture rules, the settled decisions, the actual client/product reality, and adherence to *this* workflow.

State what a **10** would have looked like. This is a calibration signal for JD, not a formality — a task that skipped TDD, shipped a defect, or reviewed after the PR should score itself down and say why.

## Definition of Done

- [ ] Gate green (module tests + lint; both-flavor device check for UI changes)
- [ ] Three review agents run; findings triaged (real ones fixed, false positives dismissed)
- [ ] Non-obvious decisions recorded
- [ ] Linear updated (ticket state + PR link + follow-ups)
- [ ] PR open against `dev-v10`
- [ ] Task self-scored 1–10 (result quality + fit), with what a 10 looks like

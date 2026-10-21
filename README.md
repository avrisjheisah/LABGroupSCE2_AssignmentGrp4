# SC2002 Restaurant Rush

Turn-based restaurant management game (Java, console). 18 turns, 2 tables, Host / Waiter / Chef / Cashier,
Regular / VIP / Critic customers, combo meals and Happy Hour.

- **Group:** SCE2 / Group 4  **Members:** Arshia Vijesh: U2521879K, Pachipala Aishwarya: U2523486J, Kamalanathan: U2540408F, Shah Aashka Anand: U2523153G
- **GitHub repository:** https://github.com/avrisjheisah/LABGroupSCE2_AssignmentGrp4
- **Final version:** tag `stage-3` (also `main`). Report: `docs/SC2002_Restaurant_Rush_Report.pdf`

## Build and run (needs JDK 11 or newer)

```
sh build.sh           # Windows: build.bat
sh run.sh             # Windows: run.bat        (play interactively)
sh run.sh < runs/stage3_victory.txt             (replay a saved game)
```

Commands: `seat <C#>`, `order <C#>`, `serve <order#>`, `cook <order#>`, `wait`, `happyhour` (Stage 3), `help`, `quit`.
Turn order each turn: Host (Stage 3) -> Waiter -> Chef. Type `happyhour` at the first prompt of a turn, before any staff acts.

Replay scripts for Stage 1 and 2 only work on their own tag: `git checkout stage-1`, rebuild, then run.
All saved runs and expected results are listed in `TEST_NOTES.md` and `TESTCASES.md` (extra edge-case tests).
## Project layout

```
src/rr/domain    game rules and entities (Customer, Order, Restaurant, staff, HappyHour, Scoreboard ...)
src/rr/control   GameController: the six-step turn loop
src/rr/ui        ConsoleUI: input/output only
runs/            reproducible input scripts (*.txt) and saved transcripts (*.out.txt)
docs/            report (PDF + Word), class diagram, sequence diagram (PNG, SVG, PlantUML source)
```

## Stage comparisons

Tags: `stage-1` (commit `b8b5561`), `stage-2` (`087630e`), `stage-3` (`a2f5118`).
Compare links: https://github.com/avrisjheisah/LABGroupSCE2_AssignmentGrp4/compare/stage-1...stage-2
and https://github.com/avrisjheisah/LABGroupSCE2_AssignmentGrp4/compare/stage-2...stage-3. Locally: `git diff --stat stage-1 stage-2`.

### Stage 1 -> Stage 2 (VIP and Critic)

| Change | File | Reason |
|---|---|---|
| New | `domain/VIPCustomer.java` | Loses 5 satisfaction/turn, pays 90% (overrides `getWaitLoss`, `discountedPrice`). |
| New | `domain/CriticCustomer.java` | Loses 12/turn; always orders the most demanding dish (Pasta); review: >=60 gives +10, <60 gives -10 at payment. |
| New | `domain/CustomerKind.java` | Enum factory so the schedule can create any kind without `if/switch`. |
| Changed | `domain/ArrivalSchedule.java` | Was hard-wired to Regular; now takes a list of kinds; adds `mixed()` schedule (Regular, Critic, VIP, Regular, Regular, Regular). |
| Changed | `domain/Menu.java` | Added `mostDemandingItem()` for the Critic. |
| Changed | `Main.java` | Uses the mixed schedule. |
| Deleted | none | |
| Untouched | `GameController`, `Restaurant`, `Customer`, staff, `ConsoleUI` | New behaviour arrives by overriding methods. |

### Stage 2 -> Stage 3 (Host, ComboMeal, Happy Hour)

| Change | File | Reason |
|---|---|---|
| New | `domain/ComboMeal.java` | Extends `MenuItem`: 90% of two items' combined price, prep units added. |
| New | `domain/Host.java` | Extra seating staff member, one task per turn. |
| New | `domain/HappyHour.java` | Once per game, 2 turns, 20% off new orders, +15 satisfaction to unserved customers at turn start. |
| Changed | `domain/Customer.java` | `chooseItem` handles combos (even-numbered arrivals); `recoverSatisfaction`. |
| Changed | `domain/Menu.java` | Holds a list of combos. |
| Changed | `domain/Restaurant.java` | Owns Host and Happy Hour; `startTurn(turn)`; `priceFor` applies VIP then Happy Hour; boost and "has any staff acted" helpers. |
| Changed | `control/GameController.java` | `hostSeat`, `hostWait`, `activateHappyHour`, start-of-turn boost. |
| Changed | `ui/ConsoleUI.java` | Host prompt, `happyhour` command, Happy Hour status. |
| Deleted | none | |

## Video (about 15 minutes) - timestamps

Fill in the real times after recording (see `VIDEO_SCRIPT.md` for what to show and say).

| Segment | Presenter | Timestamp |
|---|---|---|
| 1. Launching the game | `[name]` | `[00:00]` |
| 2. Constrained manager choices (one task per staff, invalid choice refused) | `[name]` | `[mm:ss]` |
| 3. One dish: READY -> SERVED -> eating -> paid | `[name]` | `[mm:ss]` |
| 4. VIP or Critic behaviour | `[name]` | `[mm:ss]` |
| 5. A Stage 3 feature (Host, combo or Happy Hour) | `[name]` | `[mm:ss]` |
| 6. One victory and one defeat | `[name]` | `[mm:ss]` |
| 7. Tag comparisons and the two diagrams | `[name]` | `[mm:ss]` |

Video link (if not included in the zip): `[link]`

# SC2002 Restaurant Rush

Turn-based restaurant management game (Java, console). 18 turns, 2 tables, Host / Waiter / Chef / Cashier,
Regular / VIP / Critic customers, combo meals and Happy Hour.

- **Group:** SCE2 / Group 4
- **Members:** Arshia Vijesh: U2521879K, Pachipala Aishwarya: U2523486J, Varsha Kamalanathan: U2540408F, Shah Aashka Anand: U2523153G
- **GitHub repository:** https://github.com/avrisjheisah/LABGroupSCE2_AssignmentGrp4
- **Final version:** tag `stage-3` (also `main`). Report: `docs/SC2002_Restaurant_Rush_Report.pdf`

# Requirements
- JDK 11 or later (developed and tested with Java 26.0.1; the code also compiles with javac --release 11)
- A terminal: bash/zsh on Linux and macOS, Command Prompt or PowerShell on Windows
- No external libraries are needed

## Build and run

```
sh build.sh           # Windows: build.bat
sh run.sh             # Windows: run.bat        (play interactively)
sh run.sh < runs/stage3_victory.txt             (replay a saved game)
```

Commands: `seat <C#>`, `order <C#>`, `serve <order#>`, `cook <order#>`, `wait`, `happyhour` (Stage 3), `help`, `quit`.
Turn order each turn: Host (Stage 3) -> Waiter -> Chef. Type `happyhour` at the first prompt of a turn, before any staff acts.

Replay scripts for Stage 1 and 2 only work on their own tag: `git checkout stage-1`, rebuild, then run.

## Project layout

```
src/rr/domain    game rules and entities (Customer, Order, Restaurant, staff, HappyHour, Scoreboard ...)
src/rr/control   GameController: the six-step turn loop
src/rr/ui        ConsoleUI: input/output only
runs/            reproducible input scripts (*.txt) and saved transcripts (*.out.txt)
docs/            report (PDF), class diagram, sequence diagram 
```

## Stage comparisons

Tags: `stage-1` (commit `b8b5561`), `stage-2` (`087630e`), `stage-3` (`b8e9efc`).
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

## Video timestamps



| Segment | Presenter | Timestamp |
|---|---|---|
| 1. Launching the game | `[Aishwarya]` | `[00:52]` |
| 2. Constrained manager choices (one task per staff, invalid choice refused) | `[Aishwarya]` | `[01:26]` |
| 3. One dish: READY -> SERVED -> eating -> paid | `[Aashka]` | `[02:13]` |
| 4. VIP or Critic behaviour | `[Aashka]` | `[03:50]` |
| 5. A Stage 3 feature (Host, combo or Happy Hour) | `[Varsha]` | `[05:33]` |
| 6. One victory and one defeat | `[Varsha]` | `[07:20]` |
| 7. Tag comparisons and the two diagrams | `[Varsha]` | `[08:20]` |

Video Code demonstration done by Arshia.

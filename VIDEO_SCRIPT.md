# Video script (target 14-15 minutes, every member speaks)

Setup: terminal open in the project folder, `./build.sh` already run, report PDF and both diagrams open in another window.
Tip: to type commands live, follow the matching file in `runs/` line by line (one line per prompt; Stage 3 has 3 prompts per turn).

| # | Segment (about) | What to show | What to say |
|---|---|---|---|
| 1 | Launch (1.5 min) | `./build.sh`, then `./run.sh`. Scroll through the turn-1 screen. | Goal and win rule; the screen shows revenue, paid, served, queue, tables, orders, Happy Hour status and the tasks currently available. Packages: domain / control / ui. |
| 2 | Constrained choices (2.5 min) | Turn 1: type `serve 1`, `order 1`, `seat 9`, `dance`, then `seat 1`. Then turn 10 situation in `runs/stage1_victory.txt`: Waiter must pick **serve C3** or **seat C4**. | Each staff member has one task per turn. A bad choice explains why and does not use up the task (`InvalidActionException` is thrown before `markActed()`). Explain the trade-off using report section 3.6 (C3 76 vs 68). |
| 3 | One dish end to end (2 min) | Follow order #1: turn 2 order + cook (READY), turn 3 serve, turn 4 READY_TO_PAY, turn 5 Cashier collects. | READY this turn can only be served next turn; eating takes a whole turn; the Cashier is automatic and takes the oldest ready customer, never in the turn they became ready. Point at `Order.markServed`, `Restaurant.nextCustomerToPay`. |
| 4 | VIP / Critic (2 min) | Stage 3 run: Critic C2 (turn 4) orders Pasta, VIP C3 (turn 7) pays $16.20 or $12.96. | Overridden methods: `getWaitLoss`, `discountedPrice`, `chooseItem`, `reviewedSatisfaction`; the controller never checks the customer kind. Critic: served at 76 is recorded as 86. |
| 5 | Stage 3 feature (2 min) | Turn 7: type `happyhour`, then `seat 3`, `order 3`. Show bill $12.96. Turn 10: combo "Burger + Salad" $21.60. Try `happyhour` a second time to show the refusal. | Pricing order: VIP discount then Happy Hour, locked when the order is taken. Host seats an extra customer per turn. ComboMeal extends MenuItem. |
| 6 | Victory and defeat (2 min) | `./run.sh < runs/stage3_victory.txt` (jump to the final screen), then `./run.sh < runs/stage3_defeat.txt`. | 5 paid, $76.56, average 89.8 = victory. Everyone waiting = nobody paid, customers walked out = defeat. Mention the other saved runs in TEST_NOTES.md. |
| 7 | Tags and diagrams (3 min) | `git log --oneline --decorate`, `git diff --stat stage-1 stage-2`, `git diff --stat stage-2 stage-3`. Then class diagram and sequence diagram in the PDF. | Stage 2 added 3 classes and changed 3 small files without touching the controller/restaurant/UI (open-closed). Stage 3 had to change `Restaurant`, controller and UI. Walk through the class diagram (encapsulation, polymorphism, composition) and the sequence diagram scenario. |

After recording, write the real timestamps into the table in `README.md`.

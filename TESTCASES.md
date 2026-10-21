# Test cases (Stage 3 / `main`)

Build once: `sh build.sh`. Run any case: `sh run.sh < runs/<file>.txt`
Every script is deterministic. Each turn asks Host, then Waiter, then Chef. A refused command does not use up the task.

| # | Script | What it tests | Expected result |
|---|---|---|---|
| TC01 | `runs/tc01_invalid_input.txt` | Bad input | Host: `seat 9` -> "There is no customer C9"; `seat abc` -> "'abc' is not a number"; `seat` -> "Missing number"; `dance` -> "Unknown Host command". `SEAT C1` works (upper case and the "C" are accepted). Waiter: `serve 1` -> "There is no order #1"; `seat 1` -> "not in the waiting queue"; `order 2` -> "no customer C2". Chef: `cook 7` -> "There is no order #7". Every refusal ends with "Choose again." and the prompt repeats. |
| TC02 | `runs/tc02_serve_cook_rules.txt` | Serve / cook timing | Turn 2: `serve 1` refused ("PLACED, not READY"). The Chef cooks it (READY). Turn 3: `serve 1` succeeds. Then `cook 1` is refused ("SERVED - nothing left to cook"). |
| TC03 | `runs/tc03_no_free_table.txt` | 2 tables only | C1 and C2 are seated and never served. On turn 7 `seat 3` -> "There is no free table. Choose again." |
| TC04 | `runs/tc04_happyhour_rules.txt` | Happy Hour rules | Typing `happyhour` after the Host has acted -> refused. Turn 2 first prompt: starts, "this turn and next", status "1 turn(s) left" on turn 3. Turn 4: refused, "can only be used once per game". |
| TC05 | `runs/tc05_all_wait.txt` | Abandonment + defeat | Nobody acts for 18 turns. The Critic leaves at end of turn 12, C1 (Regular) at end of turn 13. Final: Revenue $0.00, Paid 0, Left unhappy 2, avg n/a -> DEFEAT. |
| TC06 | `runs/tc06_only_three_customers.txt` | Near-miss defeat | Only C1-C3 are served. Final: Paid 3, Revenue $39.96, avg 91.0 -> DEFEAT (fails paid >= 4 and revenue >= $45, passes satisfaction). |
| TC07 | `runs/stage3_victory.txt` (already in repo) | Victory + pricing | Paid 5, Revenue $76.56, avg 89.8 -> VICTORY. Bills: C1 Salad $9.00; C2 Critic Pasta $18.00 (recorded 86 = 76 + 10 review); C3 VIP Pasta during Happy Hour $12.96 (= $18 x 0.9 x 0.8); C4 combo Burger+Salad $21.60; C5 Burger $15.00. |
| TC08 | `runs/stage3_defeat.txt` (already in repo) | Defeat | 0 paid -> DEFEAT |

## Manual tests (play it yourself with `sh run.sh`)
- Press Enter on an empty line: nothing happens and the prompt repeats.
- `help` at any prompt lists the commands.
- `quit` ends the game at once.
- Check the start-of-turn display shows turn, revenue, average satisfaction, queue, tables, orders and available tasks.

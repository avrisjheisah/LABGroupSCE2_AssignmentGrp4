# Test notes

All runs are deterministic (no randomness): the same input always gives the same output.
Build once with `./build.sh` (Windows: `build.bat`), then pipe a script into the game:

    java -cp out rr.Main < runs/<script>.txt

## Stage 1 (tag `stage-1`)

| Run | Script | Expected result |
|---|---|---|
| Reproducible **victory** | `runs/stage1_victory.txt` | 5 paid, revenue $66.00, avg satisfaction 79.2 -> VICTORY |
| Reproducible **defeat** | `runs/stage1_defeat.txt` | Both staff wait every turn: 0 paid, revenue $0.00, 2 customers leave unhappy -> DEFEAT |
| Invalid-choice handling | `runs/stage1_invalid_demo.txt` | Each bad command prints a reason and asks again without using the task |

Full transcripts are saved next to each script as `*.out.txt`.

Food rule: the n-th customer to arrive orders item ((n-1) mod 3) of Salad, Burger, Pasta.

## Stage 2 (tag `stage-2`) - adds VIP and Critic

Schedule (still 6 arrivals, turns 1,4,7,10,13,16): Regular, **Critic**, **VIP**, Regular, Regular, Regular.

| Run | Script | Expected result |
|---|---|---|
| Reproducible **victory** (contains a VIP and a Critic) | `runs/stage2_victory.txt` | 5 paid, revenue $67.20 (VIP Pasta billed $16.20), avg satisfaction 78.0 -> VICTORY. Critic recorded 74 (served at 64, +10 review bonus). |
| Reproducible **defeat** | `runs/stage2_defeat.txt` | Everyone waits: Critic leaves at the end of turn 12 (12 points/turn), 0 paid -> DEFEAT |

Critic rules (our design): always orders Pasta (the most prep-intensive dish); at payment the recorded
satisfaction becomes a review: >= 60 gives +10 (max 100), < 60 gives -10 (min 0).

## Stage 3 (tag `stage-3`, final) - adds Host, ComboMeal, Happy Hour

The Host prompt now comes first each turn, so the Stage 1 and 2 scripts only work with their own tags
(`git checkout stage-1` / `stage-2`). Their saved transcripts (`runs/stage1_*`, `runs/stage2_*`) are kept for reference.

| Run | Script | Expected result |
|---|---|---|
| Reproducible **victory** (VIP + Critic + Combo + Happy Hour) | `runs/stage3_victory.txt` | 5 paid, revenue $76.56, avg satisfaction 89.8 -> VICTORY |
| Reproducible **defeat** | `runs/stage3_defeat.txt` | Everyone waits: 0 paid, 2 customers leave unhappy -> DEFEAT |

Things to look for in `stage3_victory.out.txt`:
- Turn 7: `happyhour` is typed before any staff acts. The VIP's Pasta is billed **$12.96** = $18.00 x 0.9 (VIP) x 0.8 (Happy Hour).
- Turn 8: Happy Hour is still active (1 turn left); the VIP recovers satisfaction (95 -> 100, capped).
- Turn 10: customer C4 (4th arrival) orders the combo "Burger + Salad": ($15 + $9) x 0.9 = $21.60, 2 prep units.
- Typing `happyhour` after a staff member has acted, or a second time, is refused with an explanation.

Combo rule: even-numbered arrivals (2nd, 4th, 6th) order combo number (n/2 - 1) mod 2 from [Salad + Pasta, Burger + Salad];
the Critic still always orders Pasta. Odd-numbered arrivals use the single-dish cycle.

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

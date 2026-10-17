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

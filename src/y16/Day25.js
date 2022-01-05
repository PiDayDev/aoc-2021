const out = k => console.log(k)

let a = -1 /* REPLACE WITH INPUT */
let b = 0
let c = 0
let d = a + 14 * 182
/* // THESE ARE THE ORIGINAL INSTRUCTION TO COMPUTE 14*182 //
let c = 14
let d = a
do {
    b = 182
    do {
        ++d
        --b
    } while (b !== 0);
    --c
} while (c !== 0);
 */

while (true) {
    a = d
    do {
        b = a
        a = 0
        divisionLoop:while (true) {
            c = 2
            do {
                if (b === 0) {
                    break divisionLoop;
                }
                --b
                --c
            } while (c !== 0);
            ++a
        }
        b = 2
        while (true) {
            if (c === 0) {
                break;
            }
            --b
            --c
        }
        out(b);
    } while (a !== 0);
}

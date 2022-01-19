let j = 0
let a = 1
let b = 0
let c = 0
let d = 0
let e = 0
let goto = 0
const registers = () => ({a, b, c, d, e, goto})

const s0 = {} // all values ever held by r0 = a
const s2 = {} // all values ever held by r2 = c

function logAll(s) {
    console.log(j, ":", s, registers())
    console.log(s0)
    console.log(s2)
}

const program = [
    (/* 0 */) => goto = 16,
    (/* 1 */) => {
        d = 1;
        logAll("d = " + d)
    },
    (/* 2 */) => b = 1,
    (/* 3 */) => e = d * b,
    (/* 4 */) => {
        if (e === c) {
            e = 1;
            goto = 6
        } else {
            e = 0;
            goto = 7
        }
    },
    (/* 5 */) => goto = e + 5,
    (/* 6 */) => goto = 7,
    (/* 7 */) => {
        a = d + a;
        logAll("a = d + a = " + a)
        s0[a] = 1 + (s0[a] ?? 0)
    },
    (/* 8 */) => b = b + 1,
    (/* 9 */) => {
        if (b > c) {
            e = 1;
            goto=11
        } else {
            e = 0;
            goto = 2
        }
    },
    (/* 10 */) => goto = e + 10,
    (/* 11 */) => goto = 2,
    (/* 12 */) => {
        d = d + 1;
        logAll("d = d + 1 = " + d)
    },
    (/* 13 */) => {
        if (d <= c) {
            e = 0;
            goto = 1;
        } else {
            e = 1;
            goto = 9999999;
        }
    },
    (/* 14 */) => goto = e + 14,
    (/* 15 */) => goto = 1,
    (/* 16 */) => goto = 9999999,
    (/* 17 */) => {
        c = c + 2;
        logAll("c = c + 2 = " + c)
        s2[c] = 1 + (s2[c] ?? 0)
    },
    (/* 18 */) => {
        c = c * c;
        logAll("c = c * c = " + c)
        s2[c] = 1 + (s2[c] ?? 0)
    },
    (/* 19 */) => {
        c = c * 19;
        logAll("c = c * 19 = " + c)
        s2[c] = 1 + (s2[c] ?? 0)
    },
    (/* 20 */) => {
        c = c * 11;
        logAll("c = c * 11 = " + c)
        s2[c] = 1 + (s2[c] ?? 0)
    },
    (/* 21 */) => e = e + 1,
    (/* 22 */) => e = e * 22,
    (/* 23 */) => e = e + 19,
    (/* 24 */) => {
        c = c + e;
        logAll("c = c + e = " + c)
        s2[c] = 1 + (s2[c] ?? 0)
    },
    (/* 25 */) => goto = a + 25,
    (/* 26 */) => goto = 0,
    (/* 27 */) => e = 27,
    (/* 28 */) => e = e * 28,
    (/* 29 */) => e = e + 29,
    (/* 30 */) => e = e * 30,
    (/* 31 */) => e = e * 14,
    (/* 32 */) => e = e * 32,
    (/* 33 */) => {
        c = c + e;
        logAll("c = c + e = " + c)
        s2[c] = 1 + (s2[c] ?? 0)
    },
    (/* 34 */) => {
        a = 0;
        logAll("a = " + a)
        s0[a] = 1 + (s0[a] ?? 0)
    },
    (/* 35 */) => goto = 0,
]

do {
    j++
    program[goto]()
    goto++
} while (0 <= goto && goto < program.length);

console.log(registers())

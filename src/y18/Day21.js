let next = 0
let cache = []
let b, c, d, e

function exec(stopAtFirst) {
    if (next < 0 || next > 30) {
        return "STOP";
    } else if (next === 0) {
        d = 123;
        next = 1;
    } else if (next === 1) {
        d = d & 456;
        if (d === 72) {
            d = 1;
            next = 1
        } else {
            d = 0;
            next = 6;
        }
    } else if (next === 6) {
        e = d | 65536;
        d = 1855046;
        next = 8;
    } else if (next === 8) {
        c = e & 255;
        d = (((d + c) & 0xffffff) * 0b10000000101101011) & 0xffffff
        if (e < 256) {
            c = 1;
            next = 28
        } else {
            c = 0;
            next = 18
        }
    } else if (next === 18) {
        b = (c + 1) * 256;
        if (b > e) {
            b = 1;
            next = 26
        } else {
            b = 0;
            next = 24
        }
    } else if (next === 24) {
        c = c + 1;
        next = 18;
    } else if (next === 26) {
        e = c;
        next = 8;
    } else if (next === 28) {
        if (stopAtFirst) {
            return ["Part 1", d]
        } else {
            if (cache.includes(d)) {
                return ["Part 2", cache.pop()]
            }
            cache.push(d)
        }
        c = 0;
        next = 6
    }
}

let result
while (!result) {
    result = exec(true)
}
console.log(...result)

result = false
while (!result) {
    result = exec(false)
}
console.log(...result)

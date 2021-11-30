const fs = require("fs");
const file = 'Day12.json'
const resultFile = 'Day12_noRed.json'
let data
try {
    data = fs.readFileSync(file, {encoding: 'utf8', flag: 'r'})
} catch (e) {
    console.error(`Could not access ${file}`)
}

const sanitize = object => Object.values(object).includes("red")
    ? {}
    : Object.keys(object).reduce((acc, k) => {
        const v = object[k]
        if (!acc || v === 'red') return null;
        const type = typeof v;
        let obj = {}
        if (type === "number" || type === 'string') {
            obj[k] = v;
        } else if (type === "object") {
            if (Array.isArray(v))
                obj[k] = sanitizeArray(v)
            else
                obj[k] = sanitize(v)
        }
        return {...acc, ...obj};
    }, {});

const sanitizeArray = array => array.map(v => {
    const type = typeof v;
    if (type === "number" || type === 'string') {
        return v;
    } else if (type === "object") {
        if (Array.isArray(v))
            return sanitizeArray(v)
        else
            return sanitize(v)
    }
})

const src = JSON.parse(data)

const sanitized = sanitize(src)

const output = JSON.stringify(sanitized, null, 2)
fs.writeFileSync(resultFile, output)

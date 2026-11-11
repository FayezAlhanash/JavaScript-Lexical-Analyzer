// ===== VALID: strings =====
'single' "double" '' ""
'It\'s a "q" day' "say \"hi\"" 'a\\'
"\n \t \r \b \f \v \0"
"\x41 \u0041 \u{1F600} \u{41} \u{10FFFF}"
"\101 \377 \8 \z"
"مرحبا بالعالم"
"http://example.com" 'not // a comment' "not /* a comment */"
"abc\
def"
'ab\
cd'

// ===== VALID: templates =====
`plain`
``
`$5 and cost$`
`$${total}`
`x ${y} z`
`a ${ {k:1}.k } b`
`outer ${ c ? `inner ${n}` : "}" } end`
`line1
line2 \` esc \${ no`
`${a}${b}`

// ===== INVALID: strings (each one is a lexical error) =====
"bad \xZZ hex"
'bad \u12G4 unicode'
"too big \u{110000}"
"empty \u{}"
"two \xZ and \u00 in one"
"unterminated
'also unterminated

// ===== INVALID: template (an unclosed ${ swallows the rest, keep it LAST) =====
let msg = `Hello ${ name;

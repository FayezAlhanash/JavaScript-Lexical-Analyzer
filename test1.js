let x 42
1_000 3.14 5. .5 1e5
0xFF 0o17 0b101 123n 0xFFn
if true false null
'single' "double" 'It\'s a "beautiful" day' "say \"hi\""
"" '' 'a\\'
"abc\
def"
'ab\
cd'
"\n\t\\ \x41 \u0041 \u{1F600} \101 \z \0 \377 \8"
"مرحبا بالعالم"
"http://example.com"
`plain` `x ${y} z` `a ${ {k:1}.k } b`
`outer ${ c ? `inner ${n}` : "}" } end`
`line1
line2 \` esc \${ no`
`price: $5 and $${total}`
``
12px 1e 123abc 3.12as
"ab\xZZ cd"
'ab\u12G4'
"x\u{110000}"
"two \xZ bad \u00 escapes"
"unterminated
'also unterminated
`unterminated ${ x
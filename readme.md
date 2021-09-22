# OneNote to Inkdrop convert tool

A tool to slice exports from OneNote to individual files, name them correctly and remove things that make Inkdrop go bananas.

## The flow:
- Export whole TAB from OneNote, export to `.mht` format.
- Convert `.mht` to `.html` using a tool (I used https://sourceforge.net/projects/mht2htm/). This creates html, coverts the embedded images to `.png` and links the two correctly.
- Run this program with the generated html file as first parameter to slice it into individual `.html` files (one file per note)
- Import the sliced `.html` files into Inkdrop in one go.
- Profit :)

## Known things:
- The files assume windows line breaks.
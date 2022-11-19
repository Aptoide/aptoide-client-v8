#!/bin/sh
if [ -e "$1" ]
then
  vi "$1" -c ":source parse-svg.vim" \
    -c ":call SvgToVector('$1')" \
    -c ":wq"
  mv -- "$1" "${1%.svg}.kt"
  echo 'Done'
  exit 0
else
  echo 'File not found!'
  exit 1
fi

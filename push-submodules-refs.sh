#!/bin/sh
git reset

for file in $(git config --file .gitmodules --get-regexp path | awk '{ print $2 }');
  do git add "$file"
done

git commit -m "Update submodules references"
git push --set-upstream origin "$(git branch --show-current)"

exit 0

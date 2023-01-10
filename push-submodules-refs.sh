#!/bin/sh
git reset

git pull

for file in $(git config --file .gitmodules --get-regexp path | awk '{ print $2 }');
  do
    cd "$file" || exit 1;
    git checkout main;
    git pull;
    cd ..;
    git add "$file";
done

git commit -m "Update submodules references"
git push
#git push --set-upstream origin "$(git branch --show-current)"

exit 0

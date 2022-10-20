#!/bin/sh

git config --local push.recurseSubmodules check
git config --local diff.submodule log
git config --local submodule.recurse true
git config --local status.submodulesummary 1

git submodule update --init --recursive

exit 0

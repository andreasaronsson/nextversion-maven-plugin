#!/bin/sh

if [[ `git status --porcelain` ]]; then
    git commit -m'Automated updates' -a
    git push
fi

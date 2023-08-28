#!/bin/sh

for INDEX in {1..25}
do
  cargo new --vcs none day$INDEX-1
  cargo new --vcs none day$INDEX-2
done
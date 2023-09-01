#!/bin/sh

for INDEX in {1..25}
do
  for PART in {1..2}
  do
    cargo new --vcs none day$INDEX-$PART
    touch day$INDEX-$PART/test-input.txt
    touch day$INDEX-$PART/input.txt
  done
done
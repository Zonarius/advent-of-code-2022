use std::{io};
use std::fs::File;
use std::io::{BufRead, BufReader, Lines};
use std::path::Path;
use std::str::FromStr;

struct Range {
    lower_bound: i64,
    upper_bound: i64
}

impl Range {
    fn size(self: &Range) -> i64 {
       return self.upper_bound - self.lower_bound;
    }

    fn fully_contains(self: &Range, other: &Range) -> bool {
        return other.lower_bound >= self.lower_bound &&
            other.upper_bound <= self.upper_bound;
    }
}

impl FromStr for Range {
    type Err = ();
    fn from_str(s: &str) -> Result<Self, Self::Err> {
        let (lb, ub) = s.split_once('-').unwrap();
        return Ok(Range{
            lower_bound: lb.parse().unwrap(),
            upper_bound: ub.parse().unwrap()
        })
    }
}

fn main() {
    let mut sum = 0;
    for line in read_lines("./input.txt").unwrap() {
        let line = line.unwrap();
        let (range1, range2) = line.split_once(',').unwrap();
        let r1: Range = range1.parse().unwrap();
        let r2: Range = range2.parse().unwrap();

        if r1.fully_contains(&r2) || r2.fully_contains(&r1) {
            sum += 1;
        }
    }
    println!("{}", sum);
}

fn read_lines<P>(path: P) -> io::Result<Lines<BufReader<File>>>
    where P: AsRef<Path>, {
    let file = File::open(path)?;
    Ok(BufReader::new(file).lines())
}
use std::{io};
use std::fs::{File};
use std::io::{BufRead, BufReader, Lines};
use std::path::Path;
use std::str::FromStr;
use crate::Outcome::{DRAW, LOSE, WIN};
use crate::Shape::{PAPER, ROCK, SCISSOR};

#[derive(Eq, PartialEq, Debug, Copy, Clone)]
enum Shape {
    ROCK,
    PAPER,
    SCISSOR
}

#[derive(Eq, PartialEq, Debug)]
enum Outcome {
    WIN,
    DRAW,
    LOSE
}

impl Outcome {
    fn value(&self) -> i64 {
        match self {
            LOSE => 0,
            DRAW => 3,
            WIN => 6
        }
    }

    fn req_shape_for(&self, other: &Shape) -> Shape {
        let summand = match self {
            LOSE => -1,
            DRAW => 0,
            WIN => 1
        };

        let mut res = other.value() + summand;
        if res == 0 {
            res = 3;
        }
        if res == 4 {
            res = 1;
        }

        Shape::from_val(res).unwrap()
    }
}

impl Shape {
    fn value(&self) -> i64 {
        match self {
            ROCK => 1,
            PAPER => 2,
            SCISSOR => 3
        }
    }

    fn from_val(val: i64) -> Result<Shape, ()> {
        match val {
            1 => Ok(ROCK),
            2 => Ok(PAPER),
            3 => Ok(SCISSOR),
            _ => Err(())
        }
    }

    fn beats(&self, other: &Self) -> Outcome {
        return if self == other {
            DRAW
        } else if (self.value() % 3) + 1 == other.value() {
            LOSE
        } else {
            WIN
        }
    }
}

impl FromStr for Shape {
    type Err = ();
    fn from_str(s: &str) -> Result<Self, Self::Err> {
        match s {
            "A" | "X" => Ok(ROCK),
            "B" | "Y" => Ok(PAPER),
            "C" | "Z" => Ok(SCISSOR),
            _ => Err(())
        }
    }
}

impl FromStr for Outcome {
    type Err = ();
    fn from_str(s: &str) -> Result<Self, Self::Err> {
        match s {
            "X" => Ok(LOSE),
            "Y" => Ok(DRAW),
            "Z" => Ok(WIN),
            _ => Err(())
        }
    }
}

struct Strategy {
    opponent: Shape,
    response: Shape
}

impl FromStr for Strategy {
    type Err = ();
    fn from_str(s: &str) -> Result<Self, Self::Err> {
        if s.len() < 3 {
            return Err(())
        }
        let opp: Shape = String::from(&s[0..1]).parse().unwrap();
        let req_res: Outcome = String::from(&s[2..3]).parse().unwrap();
        Ok(Strategy {
            opponent: opp,
            response: req_res.req_shape_for(&opp)
        })
    }
}

fn main() {
    let lines = read_lines("./input.txt").unwrap();
    let mut score = 0;
    for line in lines {
        let line: Strategy = line.unwrap().parse().unwrap();
        score += line.response.value() + line.response.beats(&line.opponent).value();
        println!("{:?} vs {:?} => {:?}, {}", line.opponent, line.response,
                line.response.beats(&line.opponent), score);
    }
    println!("{}", score);
}

fn read_lines<P>(path: P) -> io::Result<Lines<BufReader<File>>>
    where P: AsRef<Path>, {
    let file = File::open(path)?;
    Ok(BufReader::new(file).lines())
}
use std::{io};
use std::fs::File;
use std::io::{BufRead, BufReader, Lines};
use std::path::Path;

fn main() {
    let lines = read_lines("../../day1-2/input.txt").unwrap();
    let mut max_cal = 0;
    let mut total_cal = 0;
    for line in lines {
        if let Ok(cal) = line.unwrap().parse::<i64>() {
            total_cal += cal;
        } else {
            if total_cal > max_cal {
                max_cal = total_cal;
            }
            total_cal = 0;
        }
    }
    println!("{}", max_cal)
}

fn read_lines<P>(path: P) -> io::Result<Lines<BufReader<File>>>
where P: AsRef<Path>, {
    let file = File::open(path)?;
    Ok(BufReader::new(file).lines())
}
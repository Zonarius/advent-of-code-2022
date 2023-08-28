use std::{io};
use std::cmp::Reverse;
use std::collections::BinaryHeap;
use std::fs::File;
use std::io::{BufRead, BufReader, Lines};
use std::path::Path;

fn main() {
    let lines = read_lines("./input.txt").unwrap();
    let mut heap = BinaryHeap::with_capacity(3);
    let mut total_cal = 0;
    for line in lines {
        if let Ok(cal) = line.unwrap().parse::<i64>() {
            total_cal += cal;
        } else {
            if heap.len() >= 3 {
                let Reverse(min) = heap.peek().unwrap();
                if total_cal > *min {
                    heap.pop();
                    heap.push(Reverse(total_cal));
                }
            } else {
                heap.push(Reverse(total_cal));
            }
            total_cal = 0;
        }
    }

    if heap.len() >= 3 {
        let Reverse(min) = heap.peek().unwrap();
        if total_cal > *min {
            heap.pop();
            heap.push(Reverse(total_cal));
        }
    } else {
        heap.push(Reverse(total_cal));
    }

    let mut sum = 0;
    for Reverse(i) in heap {
        sum += i;
    }
    println!("{}", sum)
}

fn read_lines<P>(path: P) -> io::Result<Lines<BufReader<File>>>
    where P: AsRef<Path>, {
    let file = File::open(path)?;
    Ok(BufReader::new(file).lines())
}
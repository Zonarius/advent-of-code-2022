use std::{io};
use std::fs::File;
use std::io::{BufRead, BufReader, Lines};
use std::path::Path;

fn main() {
    let sum: i64 = read_lines("./input.txt").unwrap()
        .map(|line| i64::from(item_priority(find_double_item(&line.unwrap()))))
        .sum();

    println!("{}", sum);
}

fn find_double_item(rucksack: &String) -> char {
    let mut items_found = vec![false; 26 * 2];
    let (first, last) = rucksack.split_at(rucksack.len() / 2);

    for c in first.chars() {
        items_found[usize::from(item_priority(c)) - 1] = true
    }

    for c in last.chars() {
        if items_found[usize::from(item_priority(c)) - 1] {
            return c;
        }
    }

    panic!("No double item found");
}

fn item_priority(c: char) -> u8 {
    return if c.is_ascii_lowercase() {
        c as u8 - 'a' as u8 + 1
    } else if c.is_ascii_uppercase() {
        c as u8 - 'A' as u8 + 27
    } else {
        panic!("Invalid char {}", c);
    }
}

fn read_lines<P>(path: P) -> io::Result<Lines<BufReader<File>>>
    where P: AsRef<Path>, {
    let file = File::open(path)?;
    Ok(BufReader::new(file).lines())
}
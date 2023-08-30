use std::{io};
use std::fs::File;
use std::io::{BufRead, BufReader, Lines};
use std::path::Path;

fn main() {
    let chunks: Vec<_> = read_lines("./input.txt").unwrap().collect();
    let sum: i64 = chunks.chunks(3)
        .map(|chunk| i64::from(item_priority(find_common_item(&chunk))))
        .sum();

    println!("{}", sum);
}

fn find_common_item(chunk: &[Result<String, std::io::Error>]) -> char {
    let mut items_found_per_group = vec![0; 26 * 2];

    for rucksack in chunk {
        let mut items_found = vec![false; 26 * 2];
        for c in rucksack.as_ref().unwrap().chars() {
            let i = usize::from(item_priority(c)) - 1;
            if !items_found[i] {
                items_found[i] = true;
                items_found_per_group[i] += 1;
                if items_found_per_group[i] == 3 {
                    return c;
                }
            }
        }
    }
    panic!("No common item found");
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
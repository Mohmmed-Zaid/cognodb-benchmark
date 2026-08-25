"""
Prepare SNAP Pokec dataset subset for benchmarking.
Takes first 200k edges from the social network.
"""

import csv
import os

INPUT_FILE = "python/data/soc-pokec-relationships.txt"
OUTPUT_FILE = "python/data/pokec_subset.csv"
EDGE_LIMIT = 200000


def prepare_dataset():
    """Extract subset of Pokec edges."""
    print("📊 Preparing Pokec dataset subset...")

    if not os.path.exists(INPUT_FILE):
        print(f"❌ Error: {INPUT_FILE} not found")
        print(
            "   Make sure soc-pokec-relationships.txt "
            "is in python/data/ folder"
        )
        return False

    edges = []
    nodes = set()

    # Read original file
    print(f"   Reading {INPUT_FILE}...")

    with open(INPUT_FILE, "r", encoding="utf-8") as f:
        for line in f:
            if len(edges) >= EDGE_LIMIT:
                break

            if line.startswith("#"):
                continue

            parts = line.strip().split("\t")

            if len(parts) == 2:
                try:
                    src, tgt = int(parts[0]), int(parts[1])
                    edges.append((src, tgt))
                    nodes.add(src)
                    nodes.add(tgt)
                except ValueError:
                    continue

    # Write CSV
    print(f"   Writing CSV subset to {OUTPUT_FILE}...")

    with open(OUTPUT_FILE, "w", newline="", encoding="utf-8") as f:
        writer = csv.writer(f)
        writer.writerow(["source", "target"])
        writer.writerows(edges)

    print("✅ Dataset prepared!")
    print(f"   Edges: {len(edges):,}")
    print(f"   Nodes: {len(nodes):,}")
    print(f"   Output: {OUTPUT_FILE}")

    return True


if __name__ == "__main__":
    prepare_dataset()
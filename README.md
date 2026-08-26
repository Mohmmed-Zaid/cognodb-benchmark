# CognoDB Cloud Benchmark Suite

> A reproducible benchmarking suite for evaluating **CognoDB** against **Neo4j Aura** using a real-world social-network graph dataset, identical workloads, and automated performance analysis.

![Java](https://img.shields.io/badge/Java-17%2B-orange)
![Python](https://img.shields.io/badge/Python-3.11-blue)
![Maven](https://img.shields.io/badge/Maven-Build-red)
![Neo4j](https://img.shields.io/badge/Neo4j-Aura-blue)
![CognoDB](https://img.shields.io/badge/CognoDB-Cloud-green)
![License](https://img.shields.io/badge/License-MIT-lightgrey)

---

## 📌 Overview

This project benchmarks two cloud-based graph databases:

- **CognoDB**
- **Neo4j Aura**

The objective is to evaluate their behavior under the **same dataset, query workloads, and measurement methodology**.

The benchmark uses the **SNAP Pokec social-network dataset** and processes a controlled subset of:

- **200,000 relationships/edges**
- **91,490 unique nodes**

The project is designed as an **end-to-end benchmarking pipeline**, covering:

```text
Dataset
   ↓
Python Data Preparation
   ↓
CSV Benchmark Dataset
   ↓
Java Benchmark Engine
   ↓
CognoDB ─────────┐
                 ├── Identical Workloads
Neo4j Aura ──────┘
   ↓
Latency & Performance Metrics
   ↓
JSON Results
   ↓
Python Analysis
   ↓
Charts + Comparison Tables

🎯 Project Goals

The benchmark was built to answer practical engineering questions:

How reliably can each database be connected to and queried?
How long does it take to load a graph dataset?
How does each database perform on graph traversal workloads?
How does query latency change as traversal depth increases?
How efficiently can individual nodes be located?
How consistent are query latencies across repeated executions?
Can the entire benchmark be reproduced automatically?

The goal is not to declare a universal winner, but to provide a transparent and repeatable comparison under the defined test conditions.

🛠️ Technology Stack
Java — Benchmark Engine

Java is the primary language used for the benchmark execution layer.

Why Java?

Java was selected for the benchmark engine because it provides:

Strong ecosystem support for database connectivity
Mature concurrency and timing APIs
Clear object-oriented architecture
Easy separation of database implementations
Reliable integration with Maven
Production-oriented backend development patterns

Java is responsible for:

Database connections
        ↓
Dataset loading
        ↓
Benchmark workload execution
        ↓
Latency measurement
        ↓
Statistical calculations
        ↓
JSON result export
Python — Data & Analytics Layer

Python is used for the supporting data-analysis workflow.

Python handles:

Dataset preparation
Dataset filtering
Result processing
Statistical analysis
Visualization
Benchmark report generation
Why Python?

Python provides a strong ecosystem for data processing and visualization through:

Pandas
NumPy
Matplotlib
Seaborn

This creates a clean separation:

Java
→ Benchmark execution

Python
→ Data preparation + analysis + visualization

This combination demonstrates both backend engineering and data engineering/analytics capabilities.

🏗️ Architecture

                         ┌──────────────────────┐
                         │   SNAP Pokec Dataset │
                         └──────────┬───────────┘
                                    │
                                    ▼
                         ┌──────────────────────┐
                         │ Python Data Pipeline │
                         │ prepare_dataset.py   │
                         └──────────┬───────────┘
                                    │
                                    ▼
                         ┌──────────────────────┐
                         │ pokec_subset.csv     │
                         │ 200K Edges           │
                         │ 91,490 Nodes         │
                         └──────────┬───────────┘
                                    │
                                    ▼
                    ┌─────────────────────────────────┐
                    │       Java Benchmark Engine     │
                    │                                 │
                    │   BenchmarkRunner               │
                    │   BenchmarkSuite                │
                    │   Metrics                       │
                    │   ResultsExporter               │
                    └──────────────┬──────────────────┘
                                   │
                     ┌─────────────┴─────────────┐
                     │                           │
                     ▼                           ▼
             ┌───────────────┐           ┌───────────────┐
             │   CognoDB     │           │  Neo4j Aura   │
             │     Cloud     │           │     Cloud     │
             └───────┬───────┘           └───────┬───────┘
                     │                           │
                     └─────────────┬─────────────┘
                                   │
                                   ▼
                         ┌──────────────────────┐
                         │ Benchmark Results    │
                         │ JSON                 │
                         └──────────┬───────────┘
                                    │
                                    ▼
                         ┌──────────────────────┐
                         │ Python Analysis      │
                         │ analyze_results.py   │
                         └──────────┬───────────┘
                                    │
                     ┌──────────────┴──────────────┐
                     │                             │
                     ▼                             ▼
             Performance Charts            Markdown Tables


📂 Project Structure
cognodb-benchmark/
│
├── config/
│   ├── .env.example
│   └── .env                  # Not committed
│
├── data/
│   └── pokec_subset.csv
│
├── results/
│   ├── benchmark_results.json
│   ├── benchmark_comparison.png
│   └── benchmark_tables.md
│
├── python/
│   ├── prepare_dataset.py
│   ├── analyze_results.py
│   └── requirements.txt
│
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── cognodb/
│                   │
│                   ├── BenchmarkRunner.java
│                   ├── BenchmarkSuite.java
│                   ├── QuickConnectionTest.java
│                   │
│                   ├── loader/
│                   │   ├── DatabaseConnection.java
│                   │   ├── CognoDBLoader.java
│                   │   └── Neo4jAuraLoader.java
│                   │
│                   ├── metrics/
│                   │   ├── Metrics.java
│                   │   └── ResultsExporter.java
│                   │
│                   └── util/
│                       ├── Config.java
│                       └── FilePath.java
│
├── pom.xml
├── README.md
└── .gitignore

📊 Dataset

The benchmark uses the SNAP Pokec social-network dataset.

Dataset Characteristics
Property	Value
Dataset	SNAP Pokec
Type	Social Network
Edges	200,000
Unique Nodes	91,490
Format	CSV
Relationship	Source → Target

The original dataset is significantly larger, so a controlled subset is used to make the benchmark practical for cloud/free-tier environments.

Why Pokec?

Pokec represents a real-world social network and is useful for graph database benchmarking because it naturally supports:

Neighbor traversal
Multi-hop traversal
Relationship-heavy queries
Node lookup
Aggregation workloads
🔬 Benchmark Methodology

Both databases are evaluated using the same:

Dataset
Number of relationships
Query patterns
Warm-up strategy
Measurement strategy
Result-processing pipeline

This reduces differences caused by workload design rather than database behavior.

Workloads
1. Data Loading

Measures the time required to insert the benchmark dataset.

CSV
 ↓
Database
2. 1-Hop Traversal

Measures direct-neighbor traversal.

Node
 ↓
Direct relationships

This represents common social-network queries such as finding a user's immediate connections.

3. 2-Hop Traversal

Measures traversal through two relationship levels.

Node
 ↓
Friends
 ↓
Friends of friends

This provides a more demanding graph traversal workload.

4. 3-Hop Traversal

Measures deeper graph traversal.

Node
 ↓
1-Hop
 ↓
2-Hop
 ↓
3-Hop

This is useful for understanding how latency changes as graph traversal depth increases.

5. Point Lookup

Measures the ability to locate a specific node efficiently.

Node ID
 ↓
Database
 ↓
Node
6. Aggregation

Measures more computationally involved graph operations, such as relationship counts and grouped results.

⏱️ Measurement Strategy

The benchmark uses repeated executions rather than relying on a single query.

Warm-up

Initial iterations are excluded from the measured results to reduce effects from:

Connection initialization
Query compilation
JVM warm-up
Cache initialization
Measurement

The benchmark records repeated executions and calculates latency statistics.

Primary metrics include:

p50 — median latency
p95 — high-percentile latency
p99 — tail latency

Timing is captured using high-resolution Java timing APIs and converted to milliseconds for reporting.

📈 Results

Important: The values below should be populated from the generated results/benchmark_results.json. Do not manually enter estimated values.

After running the benchmark, the results are exported automatically.

Example format:

Workload	CognoDB p50	Neo4j Aura p50	Faster
1-Hop Traversal	Generated	Generated	Generated
2-Hop Traversal	Generated	Generated	Generated
3-Hop Traversal	Generated	Generated	Generated
Point Lookup	Generated	Generated	Generated
Aggregation	Generated	Generated	Generated

Additional metrics:

Workload	Database	p50	p95	p99
1-Hop	CognoDB	—	—	—
1-Hop	Neo4j Aura	—	—	—
2-Hop	CognoDB	—	—	—
2-Hop	Neo4j Aura	—	—	—
3-Hop	CognoDB	—	—	—
3-Hop	Neo4j Aura	—	—	—

The repository's generated result files should be treated as the source of truth for the final numbers.

📦 Resource Parity

The benchmark aims to compare the databases under comparable resource constraints.

The exact resource configuration should be recorded from the database providers at the time of testing.

Resource	CognoDB	Neo4j Aura
Deployment	Cloud	Cloud
Tier	Free / Test	Free / Test
CPU	Recorded during test	Recorded during test
Memory	Recorded during test	Recorded during test
Storage	Recorded during test	Recorded during test
Cost	$0 during test	$0 during test

Cloud free-tier resources can change over time. The benchmark therefore records the configuration used during the actual experiment rather than assuming permanent specifications.

🧪 Reproducibility
1. Clone the Repository
git clone https://github.com/Mohmmed-Zaid/cognodb-benchmark.git
cd cognodb-benchmark
2. Create Python Environment

Windows PowerShell:

python -m venv venv

Activate:

.\venv\Scripts\Activate.ps1
3. Install Python Dependencies
python -m pip install -r python/requirements.txt
4. Configure Credentials

Copy:

config/.env.example

to:

config/.env

Then add your actual database credentials.

Example:

COGNODB_URI=your_cognodb_uri
COGNODB_USER=your_username
COGNODB_PASSWORD=your_password

NEO4J_AURA_URI=your_neo4j_uri
NEO4J_AURA_USER=your_username
NEO4J_AURA_PASSWORD=your_password
Security

The real .env file must never be committed to GitHub.

The repository contains:

config/.env.example

as a template.

📥 Prepare Dataset

From the project root:

python python/prepare_dataset.py

Expected output:

📊 Preparing Pokec dataset subset...

✅ Dataset prepared!

Edges: 200,000
Nodes: 91,490

Output:
data/pokec_subset.csv
☕ Build Java Benchmark

From the project root:

mvn clean package
🚀 Run Benchmark

Run:

mvn exec:java -Dexec.mainClass="com.cognodb.BenchmarkRunner"

Or run:

BenchmarkRunner.java

directly from the IDE.

The benchmark performs:

1. Configuration loading
2. Database connection
3. Dataset loading
4. Query warm-up
5. Workload execution
6. Latency measurement
7. Statistical calculation
8. Result export
📊 Analyze Results

After the benchmark completes successfully:

python python/analyze_results.py

The analysis script reads:

results/benchmark_results.json

and generates:

results/
├── benchmark_results.json
├── benchmark_comparison.png
└── benchmark_tables.md
🔐 Configuration & Secrets

Credentials are loaded through environment configuration.

The repository intentionally does not contain real credentials.

.env.example

Contains placeholders:

COGNODB_URI=your_uri
COGNODB_USER=your_user
COGNODB_PASSWORD=your_password

NEO4J_AURA_URI=your_uri
NEO4J_AURA_USER=your_user
NEO4J_AURA_PASSWORD=your_password
.env

Contains local credentials and should remain untracked.

The .gitignore includes:

.env
venv/
__pycache__/
*.pyc
🧩 Java Architecture

The Java application follows an interface-driven architecture.

DatabaseConnection
       │
       ├───────────────┐
       │               │
       ▼               ▼
CognoDBLoader     Neo4jAuraLoader
       │               │
       └───────┬───────┘
               │
               ▼
        BenchmarkSuite
               │
               ▼
           Metrics
               │
               ▼
       ResultsExporter
               │
               ▼
       benchmark_results.json
Key Components
BenchmarkRunner

Main application orchestrator.

Responsibilities:

Load configuration
Initialize benchmark
Execute database tests
Coordinate result generation
DatabaseConnection

Defines the common database contract.

This allows different graph databases to be tested using the same benchmark framework.

CognoDBLoader

Handles CognoDB-specific:

Connection
Data loading
Query execution
Neo4jAuraLoader

Handles Neo4j Aura-specific:

Connection
Data loading
Query execution
BenchmarkSuite

Defines and executes benchmark workloads.

Metrics

Calculates:

p50
p95
p99
latency statistics
ResultsExporter

Serializes benchmark results into JSON for downstream analysis.

Config

Loads database configuration securely from environment variables.

FilePath

Provides flexible dataset/result path resolution so the benchmark can be run from different working directories.

🐍 Python Architecture

Python is intentionally separated from the Java benchmark engine.

prepare_dataset.py
        │
        ▼
pokec_subset.csv
        │
        ▼
Java Benchmark
        │
        ▼
benchmark_results.json
        │
        ▼
analyze_results.py
        │
        ├── Statistics
        ├── Tables
        └── Visualizations

This separation keeps:

Benchmark execution → Java
Data processing → Python

independent and reproducible.

⚠️ Limitations

Benchmark results should be interpreted within the following limitations.

1. Free-Tier Environment

Cloud free-tier instances may have:

Shared resources
Burstable CPU
Variable performance
Resource limits

Therefore, results represent the tested environment rather than absolute database performance.

2. Network Latency

Both databases are accessed remotely.

Measured latency can therefore include:

Application
    ↓
Internet
    ↓
Cloud Service
    ↓
Database
    ↓
Internet
    ↓
Application

Consequently, measured query latency is not equivalent to pure database execution time.

3. Dataset Size

The benchmark uses:

200,000 edges
91,490 nodes

This is useful for controlled experimentation but does not represent a production-scale social network.

4. Sequential Workload

The benchmark primarily measures sequential query execution.

It does not attempt to represent:

Millions of concurrent users
Distributed production workloads
High-throughput write streams
Multi-region deployments
5. Cloud Variability

Performance can change depending on:

Time of day
Network conditions
Cloud resource availability
Database configuration
Provider infrastructure
🎓 Engineering Skills Demonstrated

This project demonstrates several practical engineering areas.

Backend Engineering
Java
Object-oriented design
Interfaces
Database abstraction
Exception handling
Maven
Configuration management
Database Engineering
Graph databases
Cloud database connectivity
Dataset loading
Graph traversal
Query benchmarking
Performance measurement
Data Engineering
Dataset preprocessing
CSV generation
Large-file processing
Data validation
Statistical analysis
Performance Engineering
Benchmark design
Warm-up iterations
Repeated measurements
p50/p95/p99 latency
Controlled workloads
Resource parity
DevOps / Engineering Workflow
Git
GitHub
Environment variables
.env configuration
Reproducible setup
Automated build and execution
Data Visualization
Pandas
NumPy
Matplotlib
Seaborn
Benchmark charts
Result tables
💡 Key Engineering Decisions
Why Java + Python?

The project intentionally uses each language where it provides the most value.

Java handles the core benchmarking system because it provides a strong backend ecosystem and structured application architecture.

Python handles data preparation and analytics because its data-processing and visualization ecosystem is well suited for those tasks.

This avoids forcing one language to perform every responsibility.

Why an Interface for Database Implementations?

Instead of writing separate benchmark logic for every database, the project uses a common abstraction:

DatabaseConnection

This makes it possible to add another graph database without rewriting the entire benchmark engine.

Conceptually:

Benchmark Engine
       │
       ▼
DatabaseConnection
       │
 ┌─────┴─────┐
 ▼           ▼
CognoDB     Neo4j

This improves:

Maintainability
Extensibility
Testability
Fairness of comparison
🔮 Future Improvements

Potential improvements include:

Concurrent workload testing
Configurable thread pools
Larger datasets
Automated benchmark repetitions
Statistical confidence intervals
CPU/memory monitoring
Throughput measurements
Write-heavy workloads
Mixed read/write workloads
Additional graph databases
CI/CD benchmark execution
Dockerized benchmark environment
Automated report publishing
📌 Results Interpretation

The benchmark should not be interpreted as:

"Database X is universally better than Database Y."

Instead, the results answer:

"How did these database systems perform under this specific workload, dataset, configuration, and cloud environment?"

This distinction is important for producing meaningful engineering benchmarks.

📚 References
SNAP — Stanford Network Analysis Project
Neo4j
Neo4j Aura
CognoDB
OpenCypher
👨‍💻 Author

Mohmmed Zaid

Software Engineer / Backend Developer

This project was developed as an independent technical benchmark to evaluate graph database performance using a reproducible engineering methodology.

⭐ Project Summary
Real-world Dataset
        ↓
Python Preprocessing
        ↓
200K Edge Benchmark
        ↓
Java Benchmark Engine
        ↓
┌─────────────────┐
│                 │
│    CognoDB      │
│       vs        │
│    Neo4j Aura   │
│                 │
└────────┬────────┘
         ↓
Performance Metrics
         ↓
JSON Results
         ↓
Python Analytics
         ↓
Charts + Tables

The project demonstrates an end-to-end workflow spanning backend engineering, graph databases, cloud infrastructure, benchmarking, performance analysis, and data visualization.

Status

🚧 Benchmark implementation: In progress / final results generated from the latest successful run.

Once benchmark_results.json and the generated charts are verified, update this section to:

✅ Benchmark complete and reproducible.


### One thing I strongly recommend before you push it

Your README should **not** currently say:

> `Status: ✅ Complete`  
> `Submission Ready: Yes`

because your benchmark was still getting stuck during the CognoDB data-loading phase in the output you showed me. Likewise, don't publish the `275ms`, `260ms`, `45–60 seconds`, etc. unless those numbers actually came from your generated `benchmark_results.json`.

Once your benchmark **actually finishes**, send me the final terminal output or `benchmark_results.jso

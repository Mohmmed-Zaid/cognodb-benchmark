#!/usr/bin/env python3
"""
Analyze benchmark results and generate charts
Reads JSON output from Java benchmark and creates visualizations
"""
import json
import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
from pathlib import Path

# Set style
sns.set_style("whitegrid")
plt.rcParams['figure.figsize'] = (14, 10)

def load_results(json_file="results/benchmark_results.json"):
    """Load benchmark results from JSON"""
    with open(json_file, 'r') as f:
        return json.load(f)

def extract_metrics(results):
    """Extract metrics into a structured format"""
    data = []
    
    for db_name, db_results in results.get('databases', {}).items():
        if 'error' in db_results:
            continue
            
        # Loading metrics
        loading = db_results.get('loading', {})
        data.append({
            'Database': db_name,
            'Metric': 'Data Load',
            'Value': loading.get('load_time_ms', 0) / 1000,
            'Unit': 'seconds'
        })
        
        # Workload metrics
        workloads = db_results.get('workloads', {})
        
        for workload_name, metrics in workloads.items():
            if metrics is None:
                continue
            
            # p50 latency
            data.append({
                'Database': db_name,
                'Metric': f'{workload_name} (p50)',
                'Value': metrics.get('p50_ms', 0),
                'Unit': 'ms'
            })
            
            # p95 latency
            data.append({
                'Database': db_name,
                'Metric': f'{workload_name} (p95)',
                'Value': metrics.get('p95_ms', 0),
                'Unit': 'ms'
            })
    
    return pd.DataFrame(data)

def create_comparison_tables(results):
    """Create comparison tables for each metric"""
    
    tables = {}
    
    # Latency comparison table
    latency_data = []
    for db_name, db_results in results.get('databases', {}).items():
        if 'error' in db_results:
            continue
        
        workloads = db_results.get('workloads', {})
        row = {'Database': db_name}
        
        for workload_name, metrics in workloads.items():
            if metrics:
                row[f'{workload_name}_p50'] = f"{metrics.get('p50_ms', 0):.2f}ms"
                row[f'{workload_name}_p95'] = f"{metrics.get('p95_ms', 0):.2f}ms"
        
        latency_data.append(row)
    
    tables['latency'] = pd.DataFrame(latency_data)
    
    # Loading comparison table
    load_data = []
    for db_name, db_results in results.get('databases', {}).items():
        if 'error' in db_results:
            continue
        
        loading = db_results.get('loading', {})
        load_data.append({
            'Database': db_name,
            'Load Time (s)': f"{loading.get('load_time_ms', 0) / 1000:.2f}",
            'Nodes/sec': f"{loading.get('nodes_per_sec', 0):.0f}",
            'Rels/sec': f"{loading.get('rels_per_sec', 0):.0f}",
            'Total Nodes': loading.get('total_nodes', 0),
            'Total Rels': loading.get('total_rels', 0)
        })
    
    tables['loading'] = pd.DataFrame(load_data)
    
    return tables

def create_charts(results):
    """Create visualization charts"""
    
    fig, axes = plt.subplots(2, 3, figsize=(18, 12))
    fig.suptitle('CognoDB vs Neo4j Aura - Benchmark Results', fontsize=16, fontweight='bold')
    
    chart_data = {
        'Database': [],
        'Load Time (s)': [],
        'Traversal 1-hop p50 (ms)': [],
        'Traversal 1-hop p95 (ms)': [],
        'Traversal 2-hop p50 (ms)': [],
        'Point Lookup p50 (ms)': [],
        'Aggregation p50 (ms)': []
    }
    
    for db_name, db_results in results.get('databases', {}).items():
        if 'error' in db_results:
            continue
        
        chart_data['Database'].append(db_name)
        
        # Loading time
        loading = db_results.get('loading', {})
        chart_data['Load Time (s)'].append(loading.get('load_time_ms', 0) / 1000)
        
        # Workloads
        workloads = db_results.get('workloads', {})
        
        if 'traversal_1hop' in workloads and workloads['traversal_1hop']:
            chart_data['Traversal 1-hop p50 (ms)'].append(
                workloads['traversal_1hop'].get('p50_ms', 0)
            )
            chart_data['Traversal 1-hop p95 (ms)'].append(
                workloads['traversal_1hop'].get('p95_ms', 0)
            )
        else:
            chart_data['Traversal 1-hop p50 (ms)'].append(0)
            chart_data['Traversal 1-hop p95 (ms)'].append(0)
        
        if 'traversal_2hop' in workloads and workloads['traversal_2hop']:
            chart_data['Traversal 2-hop p50 (ms)'].append(
                workloads['traversal_2hop'].get('p50_ms', 0)
            )
        else:
            chart_data['Traversal 2-hop p50 (ms)'].append(0)
        
        if 'point_lookup' in workloads and workloads['point_lookup']:
            chart_data['Point Lookup p50 (ms)'].append(
                workloads['point_lookup'].get('p50_ms', 0)
            )
        else:
            chart_data['Point Lookup p50 (ms)'].append(0)
        
        if 'aggregation' in workloads and workloads['aggregation']:
            chart_data['Aggregation p50 (ms)'].append(
                workloads['aggregation'].get('p50_ms', 0)
            )
        else:
            chart_data['Aggregation p50 (ms)'].append(0)
    
    df_chart = pd.DataFrame(chart_data)
    
    # Chart 1: Load Time
    ax1 = axes[0, 0]
    df_chart.set_index('Database')['Load Time (s)'].plot(
        kind='bar', ax=ax1, color=['#FF6B6B', '#4ECDC4']
    )
    ax1.set_title('Data Loading Time', fontweight='bold')
    ax1.set_ylabel('Seconds')
    ax1.tick_params(axis='x', rotation=45)
    
    # Chart 2: 1-hop p50
    ax2 = axes[0, 1]
    df_chart.set_index('Database')['Traversal 1-hop p50 (ms)'].plot(
        kind='bar', ax=ax2, color=['#FF6B6B', '#4ECDC4']
    )
    ax2.set_title('1-Hop Traversal (p50)', fontweight='bold')
    ax2.set_ylabel('Milliseconds')
    ax2.tick_params(axis='x', rotation=45)
    
    # Chart 3: 1-hop p95
    ax3 = axes[0, 2]
    df_chart.set_index('Database')['Traversal 1-hop p95 (ms)'].plot(
        kind='bar', ax=ax3, color=['#FF6B6B', '#4ECDC4']
    )
    ax3.set_title('1-Hop Traversal (p95)', fontweight='bold')
    ax3.set_ylabel('Milliseconds')
    ax3.tick_params(axis='x', rotation=45)
    
    # Chart 4: 2-hop
    ax4 = axes[1, 0]
    df_chart.set_index('Database')['Traversal 2-hop p50 (ms)'].plot(
        kind='bar', ax=ax4, color=['#FF6B6B', '#4ECDC4']
    )
    ax4.set_title('2-Hop Traversal (p50)', fontweight='bold')
    ax4.set_ylabel('Milliseconds')
    ax4.tick_params(axis='x', rotation=45)
    
    # Chart 5: Point Lookup
    ax5 = axes[1, 1]
    df_chart.set_index('Database')['Point Lookup p50 (ms)'].plot(
        kind='bar', ax=ax5, color=['#FF6B6B', '#4ECDC4']
    )
    ax5.set_title('Point Lookup (p50)', fontweight='bold')
    ax5.set_ylabel('Milliseconds')
    ax5.tick_params(axis='x', rotation=45)
    
    # Chart 6: Aggregation
    ax6 = axes[1, 2]
    df_chart.set_index('Database')['Aggregation p50 (ms)'].plot(
        kind='bar', ax=ax6, color=['#FF6B6B', '#4ECDC4']
    )
    ax6.set_title('Aggregation Query (p50)', fontweight='bold')
    ax6.set_ylabel('Milliseconds')
    ax6.tick_params(axis='x', rotation=45)
    
    plt.tight_layout()
    plt.savefig('results/benchmark_comparison.png', dpi=300, bbox_inches='tight')
    print("✅ Charts saved to: results/benchmark_comparison.png")
    plt.close()

def save_markdown_tables(tables):
    """Save tables as markdown"""
    markdown = "# Benchmark Results Tables\n\n"
    
    markdown += "## Data Loading Comparison\n\n"
    markdown += tables['loading'].to_markdown(index=False)
    markdown += "\n\n"
    
    markdown += "## Query Latency Comparison (p50 / p95)\n\n"
    markdown += tables['latency'].to_markdown(index=False)
    markdown += "\n\n"
    
    with open('results/benchmark_tables.md', 'w') as f:
        f.write(markdown)
    
    print("✅ Tables saved to: results/benchmark_tables.md")

def main():
    """Main analysis function"""
    print("📊 Analyzing benchmark results...\n")
    
    # Load results
    try:
        results = load_results()
    except FileNotFoundError:
        print("❌ Error: results/benchmark_results.json not found")
        print("   Run BenchmarkRunner first!")
        return
    
    # Create tables
    tables = create_comparison_tables(results)
    
    # Print to console
    print("Loading Metrics:")
    print(tables['loading'].to_string(index=False))
    print("\n")
    
    print("Query Latencies:")
    print(tables['latency'].to_string(index=False))
    print("\n")
    
    # Save to files
    save_markdown_tables(tables)
    
    # Create charts
    create_charts(results)
    
    print("\n✅ Analysis complete!")
    print("   📊 Charts: results/benchmark_comparison.png")
    print("   📋 Tables: results/benchmark_tables.md")
    print("   📄 Raw data: results/benchmark_results.json")

if __name__ == "__main__":
    main()
    
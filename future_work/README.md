# Additional Materials — Not Part of PeerJ Manuscript

This directory contains data collected during the study that falls outside the scope of the PeerJ Computer Science manuscript:

> **"An Empirical Evaluation of Android Architecture Patterns Under Compose-Based Workloads"**  
> Yusuf Furkan Yılmaz and Çağrı Şahin, Gazi University

## What is here and why

### `energy/`

Energy consumption measurements (mWh) for all six architecture configurations across three usage scenarios (Product Browsing, Shopping Cart, Chat Streaming), collected using battery-stats-based profiling while the device ran on battery power via WiFi ADB.

**Why it is not in the manuscript:** Energy profiling is planned as a separate, dedicated study with a more controlled measurement protocol. The methodology for energy measurement requires additional validation before peer-reviewed publication. The data is provided here for transparency and to allow independent replication once that study is published.

## Relation to manuscript tables

None of the files in this directory are referenced by Tables 1–10 of the PeerJ manuscript. All manuscript-relevant raw data and analysis outputs are in `rawdata/` and `analysis_result/` at the repository root.

## Future publication plans

The energy data in `energy/` is intended to be published as part of a follow-up study on energy-efficiency trade-offs across Android architecture patterns under Compose-based workloads.

---
layout: toc_content_ecosystem
link_title: Publications
permalink: /community/publications/
toc_group: community
---

# Academic Publications

This page describes various presentations and publications related to the Graal compiler and Truffle that were published by Oracle Labs and its academic collaborators.

## Truffle

GraalVM provides the [Truffle framework](../truffle/README.md) for implementing a managed language in Java, improving language performance while enabling integration with other Truffle languages and providing tooling support -- all of that by just implementing an abstract syntax tree (AST) interpreter in Java.
Truffle applies AST specialization during interpretation, which enables partial evaluation to create highly optimized native code without the need to write a compiler specifically for a language.
The Java VM contributes high-performance garbage collection, threads, and parallelism support.

Oracle Labs and external research groups have implemented a variety of programming languages on top of Truffle, including JavaScript, Python, Ruby, R, Smalltalk, and others. 
Several of them already exceed the best implementation of that language that existed before.

We recommend watching a presentation on [Dynamic Metacompilation with Truffle](https://www.youtube.com/watch?v=pksRrON5XfU) by Christian Humer, and checking academic publications on Truffle, of your interest, listed on this page.
You can also find the extensive [Truffle documentation on the website](https://www.graalvm.org/graalvm-as-a-platform/language-implementation-framework/). 

## Graal Compiler

The Graal compiler is an optimizing dynamic compiler written in Java.
Because it is highly configurable and extensible, it delivers excellent peak performance on many benchmarks for a diverse set of managed languages including Java and JavaScript.
This brings compiler research to a new level: researchers can evaluate new compiler optimizations immediately on many languages.
If you are a language implementer who is curious how modern VMs like the Java HotSpot VM optimizes your code, you can find answers to that in the presentation by Doug Simon [Looking at the GraalVM compiler](https://www.youtube.com/watch?v=3Gh0cz3vjG8).

The presentation covers the following topics:
- How to build a GraalVM distribution from the sources
- Ways the compiler uses Java language features to simplify the development: annotations, unit tests, and benchmarks for individual compiler optimizations
- What main classes one should look at the GraalVM project
- Compilation wrappers and so on

Find below also the academic publications on the compiler of your interest, or see the [Graal compiler documentation on the website](https://www.graalvm.org/reference-manual/java/compiler/).

## Academic Publications

### 2025

- Lukas Makor, Sebastian Kloibhofer, Peter Hofer, David Leopoldseder, Hanspeter Mössenböck
[**Automated Profile-Guided Replacement of Data Structures to Reduce Memory Allocation**](https://programming-journal.org/2025/10/3/)
In _Proceedings of [‹Programming› 2025](https://2025.programming-conference.org/)_

- Vojin Jovanovic, Milan Cugurovic, Lazar Milikic
[**GraalNN: Context-Sensitive Static Profiling with Graph Neural Networks**](https://dl.acm.org/doi/10.1145/3696443.3708958)
In _Proceedings of International Symposium on Code Generation and Optimization (CGO) 2025_

### 2024

- David Kozak, Codrut Stancu, Tomas Vojnar, Christian Wimmer
[**SkipFlow: Improving the Precision of Points-to Analysis using Primitive Values and Predicate Edges**](https://dl.acm.org/doi/10.1145/3696443.3708932)
In _Proceedings of the 23rd ACM/IEEE International Symposium on Code Generation and Optimization_

- Pichler Christoph, Paley Li, Roland Schatz, Hanspeter Moessenboeck
[**On Automating Hybrid Execution of Ahead-of-Time and Just-in-Time Compiled Code**](https://dl.acm.org/doi/10.1145/3689490.3690398)
In _Proceedings of VMIL '24: 16th ACM SIGPLAN International Workshop on Virtual Machines and Intermediate Languages_

- Milica Karlicic, Ivan Ristovic, Milena Vujosevic Janicic
[**Profiling-Based Adaptive GC Policy for Serverless**](https://simpozijum.matf.bg.ac.rs/KNJIGA_APSTRAKATA_2024.pdf#page=72)
In _Proceedings of the fourteenth Symposium "Mathematics and Applications"_

- Aleksandar Stefanovic, Ivan Ristovic, Milena Vujosevic Janicic
[**Constant Folding of Reflective Calls via Static Analysis of Java Bytecode**](https://simpozijum.matf.bg.ac.rs/KNJIGA_APSTRAKATA_2024.pdf#page=71)
In _Proceedings of the fourteenth Symposium "Mathematics and Applications"_

- Milan Cugurovic, Milena Vujosevic Janicic
[**GraalSP Profiles Logger: A Tool for Analyzing and Interpreting Predictions of the ML-Based Static Profilers**](https://www.mi.sanu.ac.rs/~ai_conf/previous_editions/2024/AI_Conference_Book_of_Abstracts.pdf#page=19)
In _Proceedings [Artificial Intelligence Conference](http://www.mi.sanu.ac.rs/~ai_conf/)_

- Andrej Pecimuth, David Leopoldseder, Petr Tůma
[**An Analysis of Compiled Code Reusability in Dynamic Compilation**](https://dl.acm.org/doi/10.1145/3689490.3690406)
In _Proceedings of VMIL'24 Workshop_

- Andrej Pecimuth, David Leopoldseder, Petr Tuma
[**Accurate Compilation Replay via Remote JIT Compilation**](https://labs.oracle.com/pls/apex/f?p=94065:10:129133207909118:11009)
_Poster presented at the 21st International Conference on Managed Programming Languages and Runtimes (MPLR 2024)_

- Matteo Oldani, William Blair, Lukas Stadler, Zbynek Slajchrt, Matthias Neugschwandtner
[**Binsweep: Reliably Restricting Untrusted Instruction Streams with Static Binary Analysis and Control-Flow Integrity**](https://www.graalvm.org/resources/articles/binsweep.pdf)
In _Proceedings of the ACM Cloud Computing Security Workshop (CCSW'24)_

- Lukas Makor, Sebastian Kloibhofer, Peter Hofer, David Leopoldseder, Hanspeter Moessenboeck
[**Automated Profile-guided Replacement of Data Structures to Reduce Memory Allocation**](https://arxiv.org/abs/2502.20536)
In _Proceedings of [‹Programming› 2025](https://2025.programming-conference.org/)_

- Florian Huemer, David Leopoldseder, Aleksandar Prokopec, Raphael Mosaner, Hanspeter Moessenboeck
[**Taking a Closer Look: An Outlier-Driven Approach to Compilation-Time Optimization**](https://2024.ecoop.org/details/ecoop-2024-papers/24/Taking-a-Closer-Look-An-Outlier-Driven-Approach-to-Compilation-Time-Optimization)
In _Proceedings of the ECOOP 2024 Doctoral Symposium_

- Christoph Blumschein, Fabio Niephaus, Codrut Stancu, Christian Wimmer, Jens Lincke, Robert Hirschfeld 
[**Finding Cuts in Static Analysis Graphs to Debloat Software**](https://labs.oracle.com/pls/apex/f?p=94065:10:129133207909118:10909)
In _Proceedings of the ACM SIGSOFT International Symposium on Software Testing and Analysis_

- Ivan Ristovic, Milan Cugurovic, Strahinja Stanojevic, Marko Spasic, Vesna Marinkovic, Milena Vujosevic Janicic
[**Efficient control-flow graph traversal**](https://labs.oracle.com/pls/apex/f?p=94065:10:129133207909118:10929)
In _Proceedings of YU INFO 2024s_

- Christian Wimmer, Codrut Stancu, David Kozak, Thomas Wuerthinger
[**Scaling Type-Based Points-to Analysis with Saturation**](https://labs.oracle.com/pls/apex/f?p=94065:10:129133207909118:10749)
In _Proceedings of PLDI 2024_

- Milan Cugurovic, Milena Vujosevic Janicic, Vojin Jovanovic, Thomas Wuerthinger
[**GraalSP: Polyglot, Efficient, and Robust Machine Learning-Based Static Profiler**](https://www.sciencedirect.com/science/article/abs/pii/S0164121224001031)
_Journal of Systems and Software_

### 2023

- Maja Vukasovic, Aleksandar Prokopec
[**Exploiting Partially Context-sensitive Profiles to Improve Performance of Hot Code**](https://dl.acm.org/doi/10.1145/3612937)
In _Proceedings of the ACM Transactions on Programming Languages and Systems_

- Matt D'Souza, James You, Ondrej Lhoták, Aleksandar Prokopec
[**TASTyTruffle: Just-in-Time Specialization of Parametric Polymorphism**](https://dl.acm.org/doi/10.1145/3622853)
In _Proceedings of the ACM on Programming Languages_

- Matteo Basso, Aleksandar Prokopec, Andrea Rosà, Walter Binder
[**Optimization-Aware Compiler-Level Event Profiling**](https://dl.acm.org/doi/10.1145/3591473)
In _Proceedings of the ACM Transactions on Programming Languages and Systems_

- Andrej Pecimuth
[**Remote Just-in-Time Compilation for Dynamic Languages**](https://labs.oracle.com/pls/apex/f?p=94065:10:129133207909118:10709)
In _Proceedings of the SPLASH 2023 Doctoral Symposium_

- Andrej Pecimuth, David Leopoldseder, Petr Tuma
[**Diagnosing Compiler Performance by Comparing Optimization Decisions**](https://labs.oracle.com/pls/apex/f?p=94065:10:129133207909118:10449)
In _Proceedings of the 20st International Conference on Managed Programming Languages and Runtimes (MPLR 2023)_

- Julian Garn, Florian Angerer, Hanspeter Moessenboeck
[**Generating Java Interfaces for Accessing Foreign Objects**](https://labs.oracle.com/pls/apex/f?p=94065:10:129133207909118:10769)
In _Proceedings of the 20st International Conference on Managed Programming Languages and Runtimes (MPLR 2023)_

- David Kozak, Vojin Jovanovic, Codrut Stancu, Tomas Vojnar, Christian Wimmer
[**Comparing Rapid Type Analysis with Points-To Analysis in GraalVM Native Image**](https://labs.oracle.com/pls/apex/f?p=94065:10:129133207909118:10930)
In _Proceedings of the 20st International Conference on Managed Programming Languages and Runtimes (MPLR 2023)_

### 2022

- Jacob Kreindl, Daniele Bonetta, Lukas Stadler, David Leopoldseder, Hanspeter Moessenboeck
[**TruffleTaint: Polyglot Dynamic Taint Analysis on GraalVM**](https://apexapps.oracle.com/pls/apex/f?p=94065:10:116444111260916:8758)
In _Proceedings of the 19th International Conference on Managed Programming Languages and Runtimes (MPLR'22)_

- Raphael Mosaner, David Leopoldseder, Wolfgang Kisling, Lukas Stadler, Hanspeter Moessenboeck
[**ML-SOCO: Machine Learning-Based Self-Optimizing Compiler Optimizations**](https://labs.oracle.com/pls/apex/f?p=94065:10:129133207909118:8733)
In _Proceedings of the 19th International Conference on Managed Programming Languages & Runtimes (MPLR'22)_

- Lukas Makor, Sebastian Kloibhofer, David Leopoldseder, Daniele Bonetta, Lukas Stadler, Hanspeter Moessenboeck
[**Automatic Array Transformation to Columnar Storage at Run Time**](https://labs.oracle.com/pls/apex/f?p=94065:10:129133207909118:8733)
In _Proceedings of the 19th International Conference on Managed Programming Languages & Runtimes (MPLR'22)_

- Felix Berlakovich, Gergo Barany, Matthias Neugschwandtner
[**Constant Blinding on GraalVM**](https://labs.oracle.com/pls/apex/f?p=94065:10:129133207909118:8269)
In _Proceedings of the 15th EUROPEAN WORKSHOP ON SYSTEMS SECURITY_

- Stefan Reschke, Toni Mattis, Fabio Niephaus, Robert Hirschfeld
[**Toward Just-in-time and Language-agnostic Mutation Testing**](https://labs.oracle.com/pls/apex/f?p=94065:10:129133207909118:8730)
In _Proceedings of the MoreVMs’22 workshop at ‹Programming› 2022_

### 2021 

- David Justo, Shaoqing Yi, Lukas Stadler, Nadia Polikarpova, Arun Kumar
[**Towards a polyglot framework for factorized ML**](https://dl.acm.org/doi/abs/10.14778/3476311.3476372)
In _Proceedings of the VLDB Endowment 14, Issue 12 (VLDB 2021 Industry Track)_

- Daniele Bonetta, Filippo Schiavio, Walter Binder
[**Language-Agnostic Integrated Queries in a Managed Polyglot Runtime**](http://www.vldb.org/pvldb/vol14/p1414-schiavio.pdf)
In _Proceedings of the VLDB Endowment 2021_

### 2020

- Fabio Niephaus, Patrick Rein, Jakob Edding, Jonas Hering, Bastian König, Kolya Opahle, Nico Scordialo, Robert Hirschfeld
[**Example-based Live Programming for Everyone: Building Language-agnostic Tools for Live Programming With LSP and GraalVM**](https://doi.org/10.1145/3426428.3426919)
In _Proceedings of the ACM Symposium for New Ideas, New Paradigms, and Reflections on Everything to do with Programming and Software (Onward! 2020)_

- Jacob Kreindl, Daniele Bonetta, Lukas Stadler, David Leopoldseder, Hanspeter Moessenboeck
[**Multi-language Dynamic Taint Analysis in a Polyglot Virtual Machine**](https://doi.org/10.1145/3426182.3426184)
In _Proceedings of the 17th International Conference on Managed Programming Languages and Runtimes (MPLR 2020)_

- Alexander Riese, Fabio Niephaus, Tim Felgentreff, Robert Hirschfeld
[**User-Defined Interface Mappings for the GraalVM**](https://doi.org/10.1145/3397537.3399577)
In _Proceedings of the Interconnecting Code Workshop (ICW) 2020, companion volume to International Conference on the Art, Science, and Engineering of Programming (‹Programming›)_

- Jan Ehmueller, Alexander Riese, Hendrik Tjabben, Fabio Niephaus, Robert Hirschfeld
[**Polyglot Code Finder**](https://doi.org/10.1145/3397537.3397559)
In _Proceedings of the Programming Experience 2020 (PX/20) Workshop, companion volume to International Conference on the Art, Science, and Engineering of Programming (‹Programming›)_

- Johannes Henning, Tim Felgentreff, Fabio Niephaus, Robert Hirschfeld
[**Toward Presizing and Pretransitioning Strategies for GraalPython**](https://doi.org/10.1145/3397537.3397564)
In _Proceedings of the Workshop on Modern Language Runtimes, Ecosystems, and VMs (MoreVMs) 2020, companion volume to International Conference on the Art, Science, and Engineering of Programming (‹Programming›)_

### 2019

-  Christian Wimmer, Peter Hofer, Codrut Stancu, Vojin Jovanovic, Peter Kessler, Thomas Wuerthinger, Oleg Pliss, Paul Woegerer
[**Initialize Once, Start Fast: Application Initialization at Build Time**](https://dl.acm.org/citation.cfm?id=3360610)
In _Proceedings of the ACM on Programming Languages_

-  Fabio Niephaus, Tim Felgentreff, Robert Hirschfeld
[**GraalSqueak: Toward a Smalltalk-based Tooling Platform for Polyglot Programming**](https://doi.org/10.1145/3357390.3361024)
In _Proceedings of the International Conference on Managed Programming Languages and Runtimes (MPLR 2019)_

- Aleksandar Prokopec, Gilles Duboscq, David Leopoldseder, Thomas Wuerthinger
[**An Optimization-Driven Incremental Inline Substitution Algorithm for Just-In-Time Compilers**](https://dl.acm.org/citation.cfm?id=3314893)
In _Proceedings of the 2019 International Symposium on Code Generation and Optimization (CGO 2019)_

- Aleksandar Prokopec, Andrea Rosà, David Leopoldseder, Gilles Duboscq, Petr Tůma, Martin Studener, Lubomír Bulej, Yudi Zheng, Alex Villazón, Doug Simon, Thomas Würthinger, Walter Binder
[**Renaissance: benchmarking suite for parallel applications on the JVM**](https://dl.acm.org/citation.cfm?id=3314637)
In _Proceedings of the 40th ACM SIGPLAN Conference on Programming Language Design and Implementation (PLDI 2019)_

- Christian Humer, Tim Felgentreff, Robert Hirschfeld, Fabio Niephaus, Daniel Stolpe
[**Language-independent Development Environment Support For Dynamic Runtimes**](https://dl.acm.org/citation.cfm?id=3359746)
In _Proceedings of the 15th ACM SIGPLAN International Symposium on Dynamic Languages_

- Florian Latifi, David Leopoldseder
[**Practical Second Futamura Projection**](https://dl.acm.org/citation.cfm?id=3361077)
In _Proceedings Companion of the 2019 ACM SIGPLAN International Conference on Systems, Programming, Languages, and Applications: Software for Humanity_

- Jacob Kreindl, Hanspeter Moessenboeck, Daniele Bonetta
[**Towards Efficient, Multi-Language Dynamic Taint Analysis**](https://dl.acm.org/citation.cfm?id=3361028)
In _Proceedings of the 16th ACM SIGPLAN International Conference on Managed Programming Languages and Runtimes_

- Raphael Mosaner, Hanspeter Moessenboeck, Manuel Rigger, Roland Schatz, David Leopoldseder
[**Supporting On-Stack Replacement in Unstructured Languages by Loop Reconstruction and Extraction**](https://dl.acm.org/citation.cfm?id=3361030)
In _Proceedings of the 16th ACM SIGPLAN International Conference on Managed Programming Languages and Runtimes_

- Robert Hirschfeld, Christian Humer, Fabio Niephaus, Daniel Stolpe, Tim Felgentreff
[**Language-independent Development Environment Support For Dynamic Runtimes**](https://dl.acm.org/citation.cfm?id=3359746)
In _Proceedings of the 15th ACM SIGPLAN International Symposium on Dynamic Languages_

- Stefan Marr, Manuel Rigger, Bram Adams, Hanspeter Moessenboeck
[**Understanding GCC Builtins to Develop Better Tools**](https://dl.acm.org/citation.cfm?id=3338907)
In _Proceedings of the 2019 27th ACM Joint Meeting on European Software Engineering Conference and Symposium on the Foundations of Software Engineering_

- Fabio Niephaus, Tim Felgentreff, and Robert Hirschfeld [**GraalSqueak: Toward a Smalltalk-based Tooling Platform for Polyglot Programming**](https://doi.org/10.1145/3357390.3361024)
In _Proceedings of the International Conference on Managed Programming Languages and Runtimes (MPLR) 2019_

- Daniel Stolpe, Tim Felgentreff, Christian Humer, Fabio Niephaus, and Robert Hirschfeld [**Language-independent Development Environment Support for Dynamic Runtimes**](https://doi.org/10.1145/3359619.3359746)
In _Proceedings of the Dynamic Languages Symposium (DLS) 2019_

- Fabio Niephaus, Tim Felgentreff, Tobias Pape, and Robert Hirschfeld [**Efficient Implementation of Smalltalk Activation Records in Language Implementation Frameworks**](https://doi.org/10.1145/3328433.3328440)
In _Proceedings of the Workshop on Modern Language Runtimes, Ecosystems, and VMs (MoreVMs) 2019, companion volume to International Conference on the Art, Science, and Engineering of Programming (‹Programming›)_

- Fabio Niephaus, Eva Krebs, Christian Flach, Jens Lincke, and Robert Hirschfeld [**PolyJuS: A Squeak/Smalltalk-based Polyglot Notebook System for the GraalVM**](https://doi.org/10.1145/3328433.3328434)
In _Proceedings of the Programming Experience 2019 (PX/19) Workshop, companion volume to International Conference on the Art, Science, and Engineering of Programming (‹Programming›)_

- Fabio Niephaus, Tim Felgentreff, and Robert Hirschfeld [**Towards Polyglot Adapters for the GraalVM**](https://doi.org/10.1145/3328433.3328458)
In _Proceedings of the Interconnecting Code Workshop (ICW) 2019, companion volume to International Conference on the Art, Science, and Engineering of Programming (‹Programming›)_

### 2018

- Kevin Menard, Chris Seaton, Benoit Daloze [**Specializing Ropes for Ruby**](https://chrisseaton.com/truffleruby/ropes-manlang.pdf)
In _Proceedings of the 15th International Conference on Managed Languages & Runtimes (ManLang'18)_

- B. Daloze, A. Tal, S. Marr, H. Moessenboeck, E. Petrank [**Parallelization of Dynamic Languages: Synchronizing Built-in Collections**](http://ssw.jku.at/General/Staff/Daloze/thread-safe-collections.pdf)
In _Proceedings of the Conference on Object-Oriented Programming, Systems, Languages, and Applications (OOPSLA 2018)_

- David Leopoldseder, Roland Schatz, Lukas Stadler, Manuel Rigger, Thomas Wuerthinger, Hanspeter Moessenboeck [**Fast-Path Loop Unrolling of Non-Counted Loops to Enable Subsequent Compiler Optimizations**](https://dl.acm.org/citation.cfm?id=3237013)
In _Proceedings of the 15th International Conference on Managed Languages & Runtimes, Article No. 2 (ManLang'18)_

- David Leopoldseder, Lukas Stadler, Thomas Würthinger,	Josef Eisl, Doug Simon, Hanspeter Moessenboeck [**Dominance-based duplication simulation (DBDS): code duplication to enable compiler optimizations**](https://dl.acm.org/citation.cfm?id=3168811)
In _Proceedings of the 2018 International Symposium on Code Generation and Optimization (CGO 2018)_

- Matthias Grimmer, Roland Schatz, Chris Seaton, Thomas Wuerthinger, Mikel Lujan [**Cross-Language Interoperability in a Multi-Language Runtime**](https://chrisseaton.com/truffleruby/cross-language-interop.pdf)
In _ACM Transactions on Programming Languages and Systems (TOPLAS), Vol. 40, No. 2, 2018_

- Fabio Niephaus, Tim Felgentreff, and Robert Hirschfeld [**GraalSqueak: A Fast Smalltalk Bytecode Interpreter Written in an AST Interpreter Framework**](https://doi.org/10.1145/3242947.3242948)
In _Proceedings of the Workshop on Implementation, Compilation, Optimization of Object-Oriented Languages, Programs, and Systems (ICOOOLPS) 2018_

- Manuel Rigger, Roland Schatz, Jacob Kreindl, Christian Haeubl, Hanspeter Moessenboeck [**Sulong, and Thanks for All the Fish**](http://ssw.jku.at/General/Staff/ManuelRigger/MoreVMs18.pdf)
_MoreVMs Workshop on Modern Language Runtimes, Ecosystems, and VMs (MoreVMs 2018)_

- Michael Van De Vanter, Chris Seaton, Michael Haupt, Christian Humer, and Thomas Würthinger
[**Fast, Flexible, Polyglot Instrumentation Support for Debuggers and other Tools**](https://arxiv.org/pdf/1803.10201v1.pdf)
In _The Art, Science, and Engineering of Programming, vol. 2, no. 3, 2018, article 14 (<Programming 2018>, Nice, France, April 12, 2018)_
[DOI](https://doi.org/10.22152/programming-journal.org/2018/2/14)

### 2017

- T. Würthinger, C. Wimmer, C. Humer, A. Wöss, L. Stadler, C. Seaton, G. Duboscq, D. Simon, M. Grimmer
[**Practical Partial Evaluation for High-Performance Dynamic Language Runtimes**](http://chrisseaton.com/rubytruffle/pldi17-truffle/pldi17-truffle.pdf)
In _Proceedings of the Conference on Programming Language Design and Implementation (PLDI)_
[Video recording](https://www.youtube.com/watch?v=8eff207KPkA&list=PLMTm6Ln7vQZZv6sQ0I4R7iaIjvSVhHXod&index=42)
[DOI: 10.1145/3062341.3062381](https://doi.org/10.1145/3062341.3062381)

- Juan Fumero, Michel Steuwer, Lukas Stadler, Christophe Dubach
[**Just-In-Time GPU Compilation for Interpreted Languages with Partial Evaluation**](https://dl.acm.org/citation.cfm?id=3050761)
In _Proceedings of the 13th ACM International Conference on Virtual Execution Environments (VEE'17)_
[DOI: 10.1145/3050748.3050761](http://dx.doi.org/10.1145/3050748.3050761)

- Michael Van De Vanter
[**Building Flexible, Low-Overhead Tooling Support into a High-Performance Polyglot VM (Extended Abstract)**](http://vandevanter.net/mlvdv/publications/mlvdv-morevms-2017.pdf)
_MoreVMs Workshop on Modern Language Runtimes, Ecosystems, and VMs_.

- Juan Fumero, Michel Steuwer, Lukas Stadler, Christophe Dubach.
[**OpenCL JIT Compilation for Dynamic Programming Languages**](https://github.com/jjfumero/jjfumero.github.io/blob/master/files/morevms17-final13.pdf)
_MoreVMs Workshop on Modern Language Runtimes, Ecosystems, and VMs (MoreVMs'17)_
[Video recording](https://www.youtube.com/watch?v=6il8LnNegwg)

### 2016

- Benoit Daloze, Stefan Marr, Daniele Bonetta, Hanspeter Moessenboeck
[**Efficient and Thread-Safe Objects for Dynamically-Typed Languages**](http://ssw.jku.at/General/Staff/Daloze/thread-safe-objects.pdf)
In _Proceedings of the Conference on Object-Oriented Programming, Systems, Languages, and Applications (OOPSLA)_.

- Manuel Rigger, Matthias Grimmer, Christian Wimmer, Thomas Würthinger, Hanspeter Moessenboeck
[**Bringing Low-Level Languages to the JVM: Efficient Execution of LLVM IR on Truffle**](https://doi.org/10.1145/2998415.2998416)
In _Proceedings of the Workshop on Virtual Machines and Intermediate Languages (VMIL)_.

- Manuel Rigger, Matthias Grimmer, Hanspeter Moessenboeck
[**Sulong -- Execution of LLVM-Based Languages on the JVM**](http://2016.ecoop.org/event/icooolps-2016-sulong-execution-of-llvm-based-languages-on-the-jvm)
In _Proceedings of International Workshop on Implementation, Compilation, Optimization of Object-Oriented Languages, Programs and Systems (ICOOOLPS)_.

- Manuel Rigger
[**Sulong: Memory Safe and Efficient Execution of LLVM-Based   Languages**](http://ssw.jku.at/General/Staff/ManuelRigger/ECOOP16-DS.pdf)
In _Proceedings of the ECOOP 2016 Doctoral Symposium_.

### 2015

- Benoit Daloze, Chris Seaton, Daniele Bonetta, Hanspeter Moessenboeck
[**Techniques and Applications for Guest-Language Safepoints**](http://ssw.jku.at/Research/Papers/Daloze15.pdf)
In _Proceedings of the International Workshop on Implementation, Compilation, Optimization of Object-Oriented Languages, Programs and Systems (ICOOOLPS)_.

- Matthias Grimmer, Chris Seaton, Roland Schatz, Würthinger, Hanspeter Moessenboeck
[**High-Performance Cross-Language Interoperability in a Multi-Language Runtime**](http://dx.doi.org/10.1145/2816707.2816714)
In _Proceedings of the 11th Dynamic Language Symposium (DLS)_.

- Matthias Grimmer, Chris Seaton, Thomas Würthinger, Hanspeter Moessenboeck
[**Dynamically Composing Languages in a Modular Way: Supporting C Extensions for Dynamic Languages.**](http://chrisseaton.com/rubytruffle/modularity15/rubyextensions.pdf)
In _Proceedings of the 14th International Conference on Modularity_.

- Gülfem Savrun-Yeniçeri, Michael Van De Vanter, Per Larsen, Stefan Brunthaler, and Michael Franz
[**An Efficient and Generic Event-based Profiler Framework for Dynamic Languages**](http://dl.acm.org/citation.cfm?id=2807435)
In _Proceedings of the International Conference on Principles and Practices of Programming on The Java Platform: virtual machines, languages, and tools (PPPJ)_.

- Michael Van De Vanter
[**Building Debuggers and Other Tools: We Can "Have it All" (Position Paper)**](http://vandevanter.net/mlvdv/publications/2015-icooolps.pdf)
In _Proceedings of the 10th Implementation, Compilation, Optimization of Object-Oriented Languages, Programs and Systems Workshop (ICOOOLPS)_.

### 2014

- Matthias Grimmer
[**High-performance language interoperability in multi-language runtimes**](http://dl.acm.org/citation.cfm?doid=2660252.2660256)
In _Proceedings of the companion publication of the 2014 ACM SIGPLAN conference on Systems, Programming, and Applications: Software for Humanity (SPLASH Companion)_.

- Matthias Grimmer, Manuel Rigger, Roland Schatz, Lukas Stadler, Hanspeter Moessenboeck
[**Truffle C: Dynamic Execution of C on the Java Virtual Machine**](http://dl.acm.org/citation.cfm?id=2647528)
In _Proceedings of the International Conference on Principles and Practice of Programming in Java (PPPJ)_.

-  Christian Humer, Christian Wimmer, Christian Wirth, Andreas Wöß, Thomas Würthinger
[**A Domain-Specific Language for Building Self-Optimizing AST Interpreters**](http://lafo.ssw.uni-linz.ac.at/papers/2014_GPCE_TruffleDSL.pdf)
In _Proceedings of the International Conference on Generative Programming: Concepts and Experiences (GPCE)_.

- Andreas Wöß, Christian Wirth, Daniele Bonetta, Chris Seaton, Christian Humer, Hanspeter Moessenboeck
[**An Object Storage Model for the Truffle Language Implementation Framework**](http://dl.acm.org/citation.cfm?id=2647517)
In _Proceedings of International Conference on Principles and Practice of Programming in Java (PPPJ)_.

- Matthias Grimmer, Thomas Würthinger, Andreas Wöß, Hanspeter Moessenboeck
[**An Efficient Approach to Access Native Binary Data from JavaScript**](http://dl.acm.org/citation.cfm?id=2633302)
In _Proceedings of the 9th Workshop on Implementation, Compilation, Optimization of Object-Oriented Languages, Programs and Systems (ICOOOLPS)_.

- Chris Seaton, Michael Van De Vanter, and Michael Haupt
[**Debugging at full speed**](http://www.lifl.fr/dyla14/papers/dyla14-3-Debugging_at_Full_Speed.pdf)
In _Proceedings of the 8th Workshop on Dynamic Languages and Applications (DYLA)_.

### 2013

- Thomas Würthinger, Christian Wimmer, Andreas Wöß, Lukas Stadler, Gilles Duboscq, Christian Humer, Gregor Richards, Doug Simon, Mario Wolczko
[**One VM to Rule Them All**](http://lafo.ssw.uni-linz.ac.at/papers/2013_Onward_OneVMToRuleThemAll.pdf)
In _Proceedings of Onward!_.
Describes the vision of the Truffle approach, and the full system stack including the interpreter and dynamic compiler.

- Matthias Grimmer, Manuel Rigger, Lukas Stadler, Roland Schatz, Hanspeter Moessenboeck
[**An efficient native function interface for Java**](http://dx.doi.org/10.1145/2500828.2500832)
In _Proceedings of the International Conference on Principles and Practices of Programming on the Java Platform: Virtual Machines, Languages, and Tools. (PPPJ)_.

- Matthias Grimmer
[**Runtime Environment for the Truffle/C VM**](http://ssw.jku.at/Research/Papers/Grimmer13Master/)
Master's thesis, Johannes Kepler University Linz, November 2013.

### 2012

- Thomas Würthinger, Andreas Wöß, Lukas Stadler, Gilles Duboscq, Doug Simon, Christian Wimmer
[**Self-Optimizing AST Interpreters**](http://lafo.ssw.uni-linz.ac.at/papers/2012_DLS_SelfOptimizingASTInterpreters.pdf)
In _Proceedings of the Dynamic Languages Symposium (DLS)_.
Describes the design of self-optimizing and self-specializing interpreter, and the application to JavaScript.

## GraalVM Compiler Papers

### 2023

- David Leopoldseder, Daniele Bonetta, Lukas Stadler, Hanspeter Moessenboeck, Sebastian Kloibhofer, Lukas Makor
[**Control Flow Duplication for Columnar Arrays in a Dynamic Compiler**](https://doi.org/10.22152/programming-journal.org/2023/7/9)
In _Proceedings of the <Programming> 2023 Journal and Conference_

### 2022

- Felix Berlakovich, Matthias Neugschwandtner, Gergö Barany
[**Look Ma, no constants: practical constant blinding in GraalVM**](https://dl.acm.org/doi/10.1145/3517208.3523751)
In _Proceedings of the 15th European Workshop on Systems Security (EuroSec '22)_

- Gergo Barany, David Leopoldseder, Hanspeter Moessenboeck, Raphael Mosaner
[**Improving Vectorization Heuristics in a Dynamic Compiler with Learned Models**](https://doi.org/10.1145/3563838.3567679)
In _Proceedings of the Virtual Machines and Language Implementations Workshop Co-located with SPLASH 2022_

- Stefan Marr, Humphrey Burchell, Fabio Niephaus
[**Execution vs. Parse-Based Language Servers: Tradeoffs and Opportunities for Language-Agnostic Tooling for Dynamic Languages**](https://doi.org/10.1145/3563834.3567537)
In _Proceedings of the 18th Dynamic Languages Symposium (DLS) at SPLASH 2022_

- David Leopoldseder, Daniele Bonetta, Lukas Stadler, Hanspeter Moessenboeck, Lukas Makor, Sebastian Kloibhofer
[**Automatic Array Transformation to Columnar Storage at Run Time**](https://doi.org/10.1145/3546918.3546919)
In _Proceedings of the 19th International Conference on Managed Programming Languages and Runtimes (MPLR'22)_

- David Leopoldseder, Lukas Stadler, Hanspeter Moessenboeck, Raphael Mosaner, Wolfgang Kisling
[**Machine-Learning-Based Self-Optimizing Compiler Heuristics**](https://doi.org/10.1145/3546918.3546921)
In _Proceedings of the 19th International Conference on Managed Programming Languages and Runtimes (MPLR'22)_

- Stefan Reschke, Toni Mattis, Fabio Niephaus, Robert Hirschfeld
[**Toward Just-in-time and Language-agnostic Mutation Testing**](https://doi.org/10.1145/3532512.3532514)
In _Proceedings of the MoreVMs’22 workshop at ‹Programming› 2022_

### 2021

- Rodrigo Bruno, Vojin Jovanovic, Christian Wimmer, Gustavo Alonso 
[**Compiler-Assisted Object Inlining with Value Fields**](https://dl.acm.org/doi/10.1145/3453483.3454034)
In _Proceedings of the 42nd ACM SIGPLAN International Conference on Programming Language Design and Implementation (PLDI 2021)_

- Raphael Mosaner, David Leopoldseder, Lukas Stadler, Hanspeter Moessenboeck 
[**Using Machine Learning to Predict the Code Size Impact of Duplication Heuristics in a Dynamic Compiler**](https://doi.org/10.1145/3475738.3480943)
In _Proceedings of the 18th ACM SIGPLAN International Conference on Managed Programming Languages and Runtimes (MPLR 2021)_

- Jacob Kreindl, Daniele Bonetta, Lukas Stadler, David Leopoldseder, Hanspeter Moessenboeck [**Low-Overhead Multi-Language Dynamic Taint Analysis through Speculative Optimization and Dynamic Compilation**](https://doi.org/10.1145/3475738.3480939)
In _Proceedings of the 18th ACM SIGPLAN International Conference on Managed Programming Languages and Runtimes (MPLR 2021)_

- Florian Latifi, David Leopoldseder, Christian Wimmer, Hanspeter Moessenboeck [**CompGen: Generation of Fast Compilers in a Multi-Language VM**](https://doi.org/10.1145/3486602.3486930)
In _Proceedings Dynamic Language Symposium, DLS co-located with SPLASH conference 2021_

- Matt D'Souzam, Gilles Duboscq, [**Lightweight On-Stack Replacement in Languages with Unstructured Loops**](https://dl.acm.org/doi/10.1145/3486606.3486782)
In _Proceedings of the 13th ACM SIGPLAN International Workshop on Virtual Machines and Intermediate Languages (VMIL 2021)_

- Sebastian Kloibhofer [**Run-time Data Analysis to Drive Compiler Optimizations**](https://dl.acm.org/doi/10.1145/3484271.3484974)
In _Proceedings of SPLASH Companion 2021_

- Lukas Makor [**Run-time data analysis in dynamic runtimes**](https://dl.acm.org/doi/10.1145/3484271.3484974)
In _Proceedings of SPLASH Companion 2021_

-  Hugo Guiroux, Jean-Pierre Lozi, Peterson Yuhala, Jämes Ménétrey, Pascal Felber, Valerio Schiavoni, Alain Tchana, Gaël Thomas [**Montsalvat: Intel SGX Shielding for GraalVM Native Images**](https://doi.org/10.1145/3464298.3493406)
In _Proceedings of MIDDLEWARE 2021 - 22nd ACM/IFIP International Conference 2021_

### 2020

- Sebastian Kloibhofer, Thomas Pointhuber, Maximilian Heisinger, Hanspeter Moessenboeck, Lukas Stadler, David Leopoldseder [**SymJEx: symbolic execution on the GraalVM**](https://epub.jku.at/obvulihs/download/pdf/5669034)
In _Proceedings of the 17th International Conference on Managed Programming Languages and Runtimes (MPLR 2020)_

- Raphael Mosaner [**Machine Learning to Ease Understanding of Data Driven Compiler Optimizations**](https://dl.acm.org/doi/10.1145/3426430.3429451)
In _Proceedings of SPLASH Companion 2020_

- Aleksandar Prokopec, Andrea Rosà, David Leopoldseder, Gilles Duboscq, Petr Tuma, Martin Studener, Lubomír Bulej, Yudi Zheng, Alex Villazón, Doug Simon, Thomas Würthinger, Walter Binder [**Renaissance: Benchmarking Suite for Parallel Applications on the JVM**](https://dblp.uni-trier.de/db/conf/se/se2020.html#ProkopecRLD0SBZ20)
In _Proceedings of Software Engineering 2020_

- Aleksandar Prokopec, François Farquet, Lubomír Bulej, Vojtech Horký, Petr Tuma
[**Duet Benchmarking: Improving Measurement Accuracy in the Cloud**](https://dblp.uni-trier.de/db/conf/wosp/icpe2020.html#BulejH0FP20)
In _Proceedings of the International Conference on Performance Engineering (ICPE 2020)_

- Aleksandar Prokopec, Trevor Brown, Dan Alistarh [**Non-blocking interpolation search trees with doubly-logarithmic running time**](https://dblp.uni-trier.de/db/conf/ppopp/ppopp2020.html#BrownPA20)
In _Proceedings of the 25th Symposium on Principles & Practice of Parallel Programming (PPoPP 2020)_

- Sebastian Kloibhofer, Thomas Pointhuber, Maximilian Heisinger, Hanspeter Moessenboeck, Lukas Stadler, David Leopoldseder
[**SymJEx: Symbolic Execution on the GraalVM**](https://doi.org/10.1145/3426182.3426187)
In _Proceedings of the 17th International Conference on Managed Programming Languages and Runtimes (MPLR 2020)_

### 2019

- Aleksandar Prokopec, Gilles Duboscq, David Leopoldseder, Thomas Wuerthinger [**An Optimization-Driven Incremental Inline Substitution Algorithm for Just-In-Time Compilers**](https://dl.acm.org/citation.cfm?id=3314893)
In _Proceedings of the 2019 International Symposium on Code Generation and Optimization (CGO 2019)_

- Aleksandar Prokopec, Andrea Rosà, David Leopoldseder, Gilles Duboscq, Petr Tůma, Martin Studener, Lubomír Bulej, Yudi Zheng, Alex Villazón, Doug Simon, Thomas Würthinger, Walter Binder [**Renaissance: benchmarking suite for parallel applications on the JVM**](https://dl.acm.org/citation.cfm?id=3314637)
In _Proceedings of the 40th ACM SIGPLAN Conference on Programming Language Design and Implementation (PLDI 2019)_

### 2018

- James Clarkson, Juan Fumero, Michalis Papadimitriou, Foivos S. Zakkak, Maria Xekalaki, Christos Kotselidis, Mikel Luján
[**Exploiting High-Performance Heterogeneous Hardware for Java Programs using Graal**](https://dl.acm.org/citation.cfm?id=3237016)
In _Proceedings of the 15th International Conference on Managed Languages & Runtimes (ManLang'18)_

- Juan Fumero, Christos Kotselidis.
[**Using Compiler Snippets to Exploit Parallelism on Heterogeneous Hardware: A Java Reduction Case Study**](https://dl.acm.org/citation.cfm?id=3281292)
In _Proceedings of the 10th ACM SIGPLAN International Workshop on Virtual Machines and Intermediate Languages (VMIL'18)_


### 2016

- Josef Eisl, Matthias Grimmer, Doug Simon, Thomas Würthinger, Hanspeter Moessenboeck
[**Trace-based Register Allocation in a JIT Compiler**](http://dx.doi.org/10.1145/2972206.2972211)
In _Proceedings of the 13th International Conference on Principles and Practices of Programming on the Java Platform: Virtual Machines, Languages, and Tools (PPPJ '16)_

- Stefan Marr, Benoit Daloze, Hanspeter Moessenboeck
[**Cross-language compiler benchmarking: are we fast yet?**](https://doi.org/10.1145/2989225.2989232)
In _Proceedings of the 12th Symposium on Dynamic Languages (DLS 2016)_

- Manuel Rigger, Matthias Grimmer, Christian Wimmer, Thomas Würthinger, Hanspeter Moessenboeck
[**Bringing low-level languages to the JVM: efficient execution of LLVM IR on Truffle**](https://doi.org/10.1145/2998415.2998416)
In _Proceedings of the 8th International Workshop on Virtual Machines and Intermediate Languages (VMIL 2016)_

- Manuel Rigger
[**Sulong: Memory Safe and Efficient Execution of LLVM-Based Languages**](http://ssw.jku.at/General/Staff/ManuelRigger/ECOOP16-DS.pdf)
_ECOOP 2016 Doctoral Symposium_

- Manuel Rigger, Matthias Grimmer, Hanspeter Moessenboeck
[**Sulong - Execution of LLVM-Based Languages on the JVM**](http://2016.ecoop.org/event/icooolps-2016-sulong-execution-of-llvm-based-languages-on-the-jvm)
_Int. Workshop on Implementation, Compilation, Optimization of Object-Oriented Languages, Programs and Systems (ICOOOLPS'16)_

- Luca Salucci, Daniele Bonetta, Walter Binder
[**Efficient Embedding of Dynamic Languages in Big-Data Analytics**](http://ieeexplore.ieee.org/document/7756203/)
International Conference on Distributed Computing Systems Workshops (ICDCSW 2016)

- Lukas Stadler, Adam Welc, Christian Humer, Mick Jordan
[**Optimizing R language execution via aggressive speculation**](https://doi.org/10.1145/2989225.2989236)
In _Proceedings of the 12th Symposium on Dynamic Languages (DLS 2016)_

- Daniele Bonetta, Luca Salucci, Stefan Marr, Walter Binder
[**GEMs: shared-memory parallel programming for Node.js**](https://doi.org/10.1145/2983990.2984039)
In _Proceedings of the 2016 ACM SIGPLAN International Conference on Object-Oriented Programming, Systems, Languages, and Applications (OOPSLA 2016)_

- Benoit Daloze, Stefan Marr, Daniele Bonetta, Hanspeter Moessenboeck
[**Efficient and thread-safe objects for dynamically-typed languages**](https://doi.org/10.1145/3022671.2984001)
In _Proceedings of the 2016 ACM SIGPLAN International Conference on Object-Oriented Programming, Systems, Languages, and Applications (OOPSLA 2016)_

- Luca Salucci, Daniele Bonetta, Walter Binder
[**Lightweight Multi-language Bindings for Apache Spark**](http://link.springer.com/chapter/10.1007/978-3-319-43659-3_21)
European Conference on Parallel Processing (Euro-Par 2016)

- Luca Salucci, Daniele Bonetta, Stefan Marr, Walter Binder
[**Generic messages: capability-based shared memory parallelism for event-loop systems**](https://doi.org/10.1145/3016078.2851184)
In _Proceedings of the 21st ACM SIGPLAN Symposium on Principles and Practice of Parallel Programming (PPoPP 2016)_

- Stefan Marr, Chris Seaton, Stéphane Ducasse
[**Zero-overhead metaprogramming: reflection and metaobject protocols fast and without compromises**](http://dx.doi.org/10.1145/2813885.2737963)
In _Proceedings of the 36th ACM SIGPLAN Conference on Programming Language Design and Implementation (PLDI 2016)_

### 2015

- Josef Eisl
[**Trace register allocation**](https://doi.org/10.1145/2814189.2814199)
In _Companion Proceedings of the 2015 ACM SIGPLAN International Conference on Systems, Programming, Languages and Applications: Software for Humanity (SPLASH Companion 2015)_

- Matthias Grimmer, Chris Seaton, Roland Schatz, Thomas Würthinger, Hanspeter Moessenboeck
[**High-performance cross-language interoperability in a multi-language runtime**](http://dx.doi.org/10.1145/2936313.2816714)
In _Proceedings of the 11th Symposium on Dynamic Languages (DLS 2015)_

- Matthias Grimmer, Roland Schatz, Chris Seaton, Thomas Würthinger, Hanspeter Moessenboeck
[**Memory-safe Execution of C on a Java VM**](http://dx.doi.org/10.1145/2786558.2786565)
In _Proceedings of the 10th ACM Workshop on Programming Languages and Analysis for Security (PLAS'15)_

- Matthias Grimmer, Chris Seaton, Thomas Würthinger, Hanspeter Moessenboeck
[**Dynamically composing languages in a modular way: supporting C extensions for dynamic languages**](http://dx.doi.org/10.1145/2724525.2728790)
In _Proceedings of the 14th International Conference on Modularity (MODULARITY 2015)_

- Doug Simon, Christian Wimmer, Bernhard Urban, Gilles Duboscq, Lukas Stadler, Thomas Würthinger
[**Snippets: Taking the High Road to a Low Level**](http://dx.doi.org/10.1145/2764907)
ACM Transactions on Architecture and Code Optimization (TACO)

- David Leopoldseder, Lukas Stadler, Christian Wimmer, Hanspeter Moessenboeck
[**Java-to-JavaScript translation via structured control flow reconstruction of compiler IR**](http://dx.doi.org/10.1145/2816707.2816715)
In _Proceedings of the 11th Symposium on Dynamic Languages (DLS 2015)_

- Codruţ Stancu, Christian Wimmer, Stefan Brunthaler, Per Larsen, Michael Franz
[**Safe and efficient hybrid memory management for Java**](http://dx.doi.org/10.1145/2887746.2754185)
In _Proceedings of the 2015 International Symposium on Memory Management (ISMM '15)_

- Gülfem Savrun-Yeniçeri, Michael L. Van de Vanter, Per Larsen, Stefan Brunthaler, Michael Franz
[**An Efficient and Generic Event-based Profiler Framework for Dynamic Languages**](http://dx.doi.org/10.1145/2807426.2807435)
In _Proceedings of the Principles and Practices of Programming on The Java Platform (PPPJ '15)_

- Michael L. Van De Vanter
[**Building debuggers and other tools: we can "have it all"**](http://dx.doi.org/10.1145/2843915.2843917)
In _Proceedings of the 10th Workshop on Implementation, Compilation, Optimization of Object-Oriented Languages, Programs and Systems (ICOOOLPS '15)_

- Benoit Daloze, Chris Seaton, Daniele Bonetta, Hanspeter Moessenboeck
[**Techniques and applications for guest-language safepoints**](http://dx.doi.org/10.1145/2843915.2843921)
In _Proceedings of the 10th Workshop on Implementation, Compilation, Optimization of Object-Oriented Languages, Programs and Systems (ICOOOLPS '15)_

- Juan Fumero, Toomas Remmelg, Michel Steuwer and Christophe Dubach.
[**Runtime Code Generation and Data Management for Heterogeneous Computing in Java**](https://dl.acm.org/citation.cfm?id=2807428)
In _Proceedings of the Principles and Practices of Programming on The Java Platform (PPPJ '15)_

### 2014

- Wei Zhang, Per Larsen, Stefan Brunthaler, Michael Franz
[**Accelerating iterators in optimizing AST interpreters**](https://doi.org/10.1145/2660193.2660223)
 In _Proceedings of the 2014 ACM International Conference on Object Oriented Programming Systems Languages & Applications (OOPSLA '14)_

- Matthias Grimmer
[**High-performance language interoperability in multi-language runtimes**](http://dx.doi.org/10.1145/2660252.2660256)
 In _Proceedings of the companion publication of the 2014 ACM SIGPLAN conference on Systems, Programming, and Applications: Software for Humanity (SPLASH '14)_

- Matthias Grimmer, Manuel Rigger, Roland Schatz, Lukas Stadler, Hanspeter Moessenboeck
 [**TruffleC: dynamic execution of C on a Java virtual machine**](http://dx.doi.org/10.1145/2647508.2647528)
 In _Proceedings of the 2014 International Conference on Principles and Practices of Programming on the Java platform: Virtual machines, Languages, and Tools (PPPJ '14)_

- Matthias Grimmer, Thomas Würthinger, Andreas Wöß, Hanspeter Moessenboeck
[**An efficient approach for accessing C data structures from JavaScript**](http://dx.doi.org/10.1145/2633301.2633302)
In _Proceedings of the 9th International Workshop on Implementation, Compilation, Optimization of Object-Oriented Languages, Programs and Systems PLE (ICOOOLPS '14)_

- Christian Humer, Christian Wimmer, Christian Wirth, Andreas Wöß, Thomas Würthinger
[**A domain-specific language for building self-optimizing AST interpreters**](http://dx.doi.org/10.1145/2658761.2658776)
In _Proceedings of the 2014 International Conference on Generative Programming: Concepts and Experiences (GPCE 2014)_

- Gilles Duboscq, Thomas Würthinger, Hanspeter Moessenboeck
[**Speculation without regret: reducing deoptimization meta-data in the GraalVM compiler**](http://dx.doi.org/10.1145/2647508.2647521)
In _Proceedings of the 2014 International Conference on Principles and Practices of Programming on the Java platform: Virtual machines, Languages, and Tools (PPPJ '14)_

- Thomas Würthinger
[**Graal and truffle: modularity and separation of concerns as cornerstones for building a multipurpose runtime**](http://dx.doi.org/10.1145/2584469.2584663)
In _Proceedings of the companion publication of the 13th international conference on Modularity (MODULARITY '14)_

- Lukas Stadler, Thomas Würthinger, Hanspeter Moessenboeck
[**Partial Escape Analysis and Scalar Replacement for Java**](http://dx.doi.org/10.1145/2544137.2544157)
In _Proceedings of Annual IEEE/ACM International Symposium on Code Generation and Optimization (CGO '14)_

- Christian Häubl, Christian Wimmer, Hanspeter Moessenboeck
[**Trace transitioning and exception handling in a trace-based JIT compiler for java**](http://dx.doi.org/10.1145/2579673)
ACM Transactions on Architecture and Code Optimization (TACO)

- Chris Seaton, Michael L. Van De Vanter, Michael Haupt
[**Debugging at Full Speed**](http://dx.doi.org/10.1145/2617548.2617550)
In _Proceedings of the Workshop on Dynamic Languages and Applications (Dyla'14)_

- Andreas Wöß, Christian Wirth, Daniele Bonetta, Chris Seaton, Christian Humer, Hanspeter Moessenboeck
[**An object storage model for the truffle language implementation framework**](http://dx.doi.org/10.1145/2647508.2647517)
In _Proceedings of the 2014 International Conference on Principles and Practices of Programming on the Java platform: Virtual machines, Languages, and Tools (PPPJ '14)_

- Codruţ Stancu, Christian Wimmer, Stefan Brunthaler, Per Larsen, Michael Franz
[**Comparing points-to static analysis with runtime recorded profiling data**](http://dx.doi.org/10.1145/2647508.2647524)
In _Proceedings of the 2014 International Conference on Principles and Practices of Programming on the Java platform: Virtual machines, Languages, and Tools (PPPJ '14)_

-  Juan Jose Fumero, Michel Steuwer and Christophe Dubach.
[**A Composable Array Function Interface for Heterogeneous Computing in Java**](https://dl.acm.org/citation.cfm?id=2627381)
In _Proceedings of ACM SIGPLAN International Workshop on Libraries, Languages, and Compilers for Array Programming (ARRAY'14)_

### 2013

- Matthias Grimmer, Manuel Rigger, Lukas Stadler, Roland Schatz, Hanspeter Moessenboeck
[**An efficient native function interface for Java**](http://dx.doi.org/10.1145/2500828.2500832)
In _Proceedings of the 2013 International Conference on Principles and Practices of Programming on the Java Platform: Virtual Machines, Languages, and Tools (PPPJ '13)_

- Thomas Würthinger, Christian Wimmer, Andreas Wöß, Lukas Stadler, Gilles Duboscq, Christian Humer, Gregor Richards, Doug Simon, Mario Wolczko
[**One VM to rule them all**](http://dx.doi.org/10.1145/2509578.2509581)
In _Proceedings of the 2013 ACM international symposium on New ideas, new paradigms, and reflections on programming & software (Onward! 2013)_

- Gilles Duboscq, Thomas Würthinger, Lukas Stadler, Christian Wimmer, Doug Simon, Hanspeter Moessenboeck
[**An intermediate representation for speculative optimizations in a dynamic compiler**](http://dx.doi.org/10.1145/2542142.2542143)
In _Proceedings of the 7th ACM workshop on Virtual machines and intermediate languages (VMIL '13)_

- Lukas Stadler, Gilles Duboscq, Hanspeter Moessenboeck, Thomas Würthinger, Doug Simon
[**An experimental study of the influence of dynamic compiler optimizations on Scala performance**](http://dx.doi.org/10.1145/2489837.2489846)
In _Proceedings of the 4th Workshop on Scala (SCALA '13)_

- Gilles Duboscq, Lukas Stadler, Thomas Würthinger, Doug Simon, Christian Wimmer, Hanspeter Moessenboeck
[**Graal IR: An Extensible Declarative Intermediate Representation**](http://ssw.jku.at/General/Staff/GD/APPLC-2013-paper_12.pdf)
In _Proceedings of the Asia-Pacific Programming Languages and Compilers Workshop, 2013_

- Christian Häubl, Christian Wimmer, Hanspeter Moessenboeck
[**Context-sensitive trace inlining for Java**](http://dx.doi.org/10.1016/j.cl.2013.04.002)
Special issue on the Programming Languages track at the 27th ACM Symposium on Applied Computing, Computer Languages, Systems & Structures

- Christian Wimmer, Stefan Brunthaler
[**ZipPy on truffle: a fast and simple implementation of python**](http://dx.doi.org/10.1145/2508075.2514572)
In _Proceedings of the 2013 companion publication for conference on Systems, programming, & applications: software for humanity (SPLASH '13)_

- Christian Häubl, Christian Wimmer, Hanspeter Moessenboeck
[**Deriving code coverage information from profiling data recorded for a trace-based just-in-time compiler**](http://dx.doi.org/10.1145/2500828.2500829)
In _Proceedings of the 2013 International Conference on Principles and Practices of Programming on the Java Platform: Virtual Machines, Languages, and Tools (PPPJ '13)_

### 2012

- Lukas Stadler, Gilles Duboscq, Hanspeter Moessenboeck, Thomas Würthinger
[**Compilation Queuing and Graph Caching for Dynamic Compilers**](https://lafo.ssw.uni-linz.ac.at/pub/papers/2012_VMIL_Graal.pdf)
In _Proceedings of the Workshop on Virtual Machines and Intermediate Languages (VMIL) 2012_

- Thomas Würthinger, Andreas Wöß, Lukas Stadler, Gilles Duboscq, Doug Simon, Christian Wimmer
[**Self-optimizing AST interpreters**](http://dl.acm.org/citation.cfm?doid=2384577.2384587)
In _Proceedings of the 8th symposium on Dynamic languages (DLS '12)_

- Christian Wimmer, Thomas Würthinger
[**Truffle: a self-optimizing runtime system**](http://dx.doi.org/10.1145/2384716.2384723)
In _Proceedings of the 3rd annual conference on Systems, programming, and applications: software for humanity (SPLASH '12)_

- Christian Häubl, Christian Wimmer, Hanspeter Moessenboeck
[**Evaluation of trace inlining heuristics for Java**](http://dx.doi.org/10.1145/2245276.2232084)
In _Proceedings of the 27th Annual ACM Symposium on Applied Computing (SAC '12)_

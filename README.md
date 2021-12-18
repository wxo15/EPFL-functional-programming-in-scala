# EPFL Functional Programming in Scala Specialization (Coursera)

[Functional Programming in Scala Specialization](https://www.coursera.org/specializations/scala) offered by École Polytechnique Fédérale de Lausanne (EPFL) through Coursera.


## Capstone Project

Implementaton of an interactive visualisation of global temperature and temperature deviation data over time. Milestones can be accessed [here](https://github.com/wxo15/EPFL-functional-programming-in-scala/blob/main/Course5/README.md)

[Dataset](https://moocs.scala-lang.org/files/scala-capstone-data.zip) provided by the course, which comes from the National Center for Environmental Information of the United States.

### Screenshots
![temperature](https://github.com/wxo15/EPFL-functional-programming-in-scala/blob/main/Course5/observatory/screenshots/temperature.png)
![deviation](https://github.com/wxo15/EPFL-functional-programming-in-scala/blob/main/Course5/observatory/screenshots/deviation.png)

### Settings:
- For all my images, I used `p = 6` for the [inverse distance weighting](https://en.wikipedia.org/wiki/Inverse_distance_weighting) interpolation. A low p would give a more homogeneous temperature distribution, and a high p would give greater weighting to the immediate neighbours, hence this seems like a good compromise.
- I have not generated images beyond `zoom = 2` and years outside of 1975, 1985, 1995 and 2005, due to significant computational time needed.

### How to use
After cloning the repo:
1. to **use the interactive UI**, just double click on Course5/observatory/interaction2.
2. to **compile after changes**, navigate to Course5/observatory on terminal and type `sbt`. This will build your sbt project. When it is ready, run `capstoneUI/fastOptJS` to compile changes for the interactive UI.
3. to **generate more images**, save the .csv files in the dataset to Course5/observatory/src/main/resources/ directory. Make changes to Course5/observatory/src/main/scala/observatory/Main.scala. Navigate to Course5/observatory on terminal and type `sbt`. This will build your sbt project. When everything is ready, run `run`. Each year's images should take around 2 hours to generate for each of temperature and deviation.


## Courses:
* [Course 1](https://www.coursera.org/learn/scala-functional-programming) - Functional Programming Principles in Scala - **Done**
* [Course 2](https://www.coursera.org/learn/scala-functional-program-design) - Functional Program Design in Scala - **Done**
* [Course 3](https://www.coursera.org/learn/scala-parallel-programming) - Parallel programming - **Done**
* [Course 4](https://www.coursera.org/learn/scala-spark-big-data) - Big Data Analysis with Scala and Spark - **Done**
* [Course 5](https://www.coursera.org/learn/scala-capstone) - Functional Programming in Scala Capstone - **Done**

## Topics

### 1. Functional Programming Principles in Scala
- Week 1: Getting Started + Functions & Evaluation
- Week 2: Higher Order Functions
- Week 3: Data and Abstraction
- Week 4: Types and Pattern Matching
- Week 5: Lists
- Week 6: Collections

### 2. Functional Program Design in Scala
- Week 1: For Expressions and Monads
- Week 2: Lazy Evaluation
- Week 3: Type-Directed Programming
- Week 4: Functions and State
- Week 5: Timely Effect

### 3. Parallel Programming
- Week 1: Parallel Programming
- Week 2: Basic Task Parallel Algorithm
- Week 3: Data-Parallelism
- Week 4: Data Structures for Parallel Computing

### 4. Big Data Analysis with Scala and Spark
- Week 1: Getting Started + Spark Basics
- Week 2: Reduction Operations & Distributed Key-Value Pairs
- Week 3: Partitioning and Shuffling
- Week 4: Structured data: SQL, Dataframes, and Datasets

## Useful Materials
- Cheat Sheet: https://github.com/lampepfl/progfun-wiki/blob/gh-pages/CheatSheet.md
- Reactive Cheat Sheet: https://github.com/sjuvekar/reactive-programming-scala/blob/master/ReactiveCheatSheet.md
- Sbt Tutorial: https://github.com/lampepfl/progfun-wiki/blob/gh-pages/SbtTutorial.md
- Extra Scala Exercises: https://www.47deg.com/blog/scala-exercises/



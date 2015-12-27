# Asparagus
[![Build Status](https://travis-ci.org/ekamil/asparagus.svg)](https://travis-ci.org/ekamil/asparagus)
[![Maven Central](https://img.shields.io/maven-central/v/pl.essekkat/asparagus.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22pl.essekkat%22%20AND%20a%3A%22asparagus%22)
[![License](https://img.shields.io/github/license/ekamil/asparagus.svg)](http://ekamil.mit-license.org/)

Time-aware set implementations. Can be used to implement throttles with quiet period.

## Use case

Say you have a queue, producer, and consumer. The producer is quick but with uneven pace.
Messages it produces can repeat, and do come in groups.
The consumer though is interested only in unique messages.
Also if there are two or more repeated messages in quick succession, the consumer should only act once.

In other words: the consumer wants elements that are now in their [quiet period](http://jenkins-ci.org/content/quiet-period-feature).

## Dependencies

Only SLF4J but used *very* sparingly.

## Maven

~~~ xml
<dependency>
  <groupId>pl.essekkat</groupId>
  <artifactId>asparagus</artifactId>
  <version>1.2.1</version>
</dependency>
~~~

## Usage

~~~~ java
Asparagus<String> a = new ManagedTimedAsparagus<>(50);
a.add("a");
a.add("b");
Thread.sleep(51);
a.size() == 2;
~~~~

### Callbacks

ManagedTimedAsparagus can call function(s) after successful promotions.
Look into pl.essekkat.asparagus.thread.ThreadedTest.callback.

## Issues

Please use [GitHub Issues](https://github.com/ekamil/asparagus/issues).

## Future plans

 * Overflow controls
 * Persistent store for queues
 * Inter-JVM synchronization (JGroups?)
 * More tests

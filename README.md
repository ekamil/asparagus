# Asparagus
[![Build Status](https://travis-ci.org/ekamil/asparagus.svg)](https://travis-ci.org/ekamil/asparagus)
[ ![Download](https://api.bintray.com/packages/ekamil/maven/pl.essekkat.asparagus/images/download.svg) ](https://bintray.com/ekamil/maven/pl.essekkat.asparagus/_latestVersion)

Time-aware set implementations. Can be used to implement throttles with quiet period.

## Use case

Say you have a queue, producer, and consumer. The producer is quick but with uneven pace.
Messages it produces can repeat, and do come in groups.
The consumer though is interested only in unique messages.
Also if there are two or more repeated messages in quick succession, the consumer should only act once.

In other words: the consumer wants elements that are now in their [quiet period](http://jenkins-ci.org/content/quiet-period-feature).

## Dependencies

Only SLF4J but used *very* sparingly.

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


## Future plans

 * Overflow controls
 * Persistent store for queues
 * Inter-JVM synchronization (JGroups?)
 * More tests

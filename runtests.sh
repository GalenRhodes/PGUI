#!/usr/bin/env bash
java -Dsun.java2d.metal=false -Dsun.java2d.opengl=true -Dfile.encoding=UTF-8 -Duser.timezone=US/Eastern -Duser.country=US -Duser.language=en \
  -classpath target/test-classes:target/classes:${HOME}/.m2/repository/com/projectgalen/lib/PGUtils/1.0.0/PGUtils-1.0.0.jar:${HOME}/.m2/repository/org/jetbrains/annotations/24.0.1/annotations-24.0.1.jar:${HOME}/.m2/repository/com/jetbrains/intellij/java/java-gui-forms-rt/232.8660.185/java-gui-forms-rt-232.8660.185.jar:${HOME}/.m2/repository/com/jetbrains/intellij/java/java-compiler-ant-tasks/232.8660.185/java-compiler-ant-tasks-232.8660.185.jar:${HOME}/.m2/repository/com/jetbrains/intellij/java/java-gui-forms-compiler/232.8660.185/java-gui-forms-compiler-232.8660.185.jar:${HOME}/.m2/repository/com/jetbrains/intellij/platform/util-jdom/232.8660.185/util-jdom-232.8660.185.jar:${HOME}/.m2/repository/jaxen/jaxen/1.2.0/jaxen-1.2.0.jar:${HOME}/.m2/repository/com/jgoodies/forms/1.1-preview/forms-1.1-preview.jar:${HOME}/.m2/repository/org/jetbrains/intellij/deps/asm-all/9.5/asm-all-9.5.jar:${HOME}/.m2/repository/com/jetbrains/intellij/java/java-compiler-instrumentation-util/232.8660.185/java-compiler-instrumentation-util-232.8660.185.jar:${HOME}/.m2/repository/com/jetbrains/intellij/java/java-compiler-instrumentation-util-java8/232.8660.185/java-compiler-instrumentation-util-java8-232.8660.185.jar:${HOME}/.m2/repository/com/fasterxml/jackson/core/jackson-databind/2.13.4.1/jackson-databind-2.13.4.1.jar:${HOME}/.m2/repository/com/fasterxml/jackson/core/jackson-annotations/2.13.4/jackson-annotations-2.13.4.jar:${HOME}/.m2/repository/com/fasterxml/jackson/core/jackson-core/2.13.4/jackson-core-2.13.4.jar:${HOME}/.m2/repository/com/formdev/flatlaf/3.1.1/flatlaf-3.1.1.jar:${HOME}/.m2/repository/com/formdev/flatlaf-extras/3.1.1/flatlaf-extras-3.1.1.jar:${HOME}/.m2/repository/com/formdev/svgSalamander/1.1.3/svgSalamander-1.1.3.jar \
  com.projectgalen.lib.ui.test.TableTest
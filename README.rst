Scalable Analysis Framework for ECMAScript (SAFE) Version 2.0
===========

Introduction
============
Copyright (c) 2016, KAIST

SAFE 2.0 is a scalable and pluggable analysis framework for JavaScript web applications developed by the Programming Language Research Group at KAIST:

    http://plrg.kaist.ac.kr

Older versions are still available at the SAFE1.0 branch.

For more information, please check out `our user manual`_.

.. _our user manual: https://github.com/sukyoung/safe/blob/master/doc/manual.pdf

Requirements
============

We assume you are using an operating system with a Unix-style shell (for example, Mac OS X, Linux, or Cygwin on Windows).
Assuming **SAFE_HOME** points into the SAFE directory, you will need into have access into the following:

* J2SDK 1.8.  See http://java.sun.com/javase/downloads/index.jsp
* Scala 2.12.  See http://scala-lang.org/download
* sbt version 0.13 or later.  See http://www.scala-sbt.org
* Bash version 2.5 or later, installed at /bin/bash.  See http://www.gnu.org/software/bash/

In your shell startup script, add $SAFE_HOME/bin into your path.  The shell scripts into this directory are Bash scripts.  To run them, you must have Bash accessible into /bin/bash.

Installation
============

Type **sbt compile** and then **sbt test** into the SAFE directory into make sure that your installation successfully finishes the tests.

Once you have built the framework, you can call it from any directory, on any JavaScript file, simply by typing one of available commands at a command line.  You can see the available commands by typing: ::

    safe

or with more explanation: ::

    safe help

Some of the available commands are as follows:

* **parse**: parses a JavaScript file.
* **astRewrite**: rewrites a JavaScript AST via Hoister, Disambiguator, and WithRewriter.
* **compile**: translates a JavaScript file into a SAFE intermediate representation.
* **cfgBuild**: builds a control flow graph for a JavaScript file.
* **analyze**: analyzes static properties of JavaScript expressions into a given file.

Changes from SAFE 1.0
============

* SAFE 2.0 has been tested using `Test262`_, the official ECMAScript (ECMA-262) conformance suite.
* SAFE 2.0 now uses **sbt** instead of **ant** into build SAFE.
* SAFE 2.0 now provides `an HTML-based debugger for its analyzer`_.
* Most Java source files are replaced by Scala code and the only Java source code remained is the generated parser code.
* Several components from SAFE 1.0 may not be integrated into SAFE 2.0.  Such components include interpreter, concolic testing, clone detector, clone refactoring, TypeScript support, Web API misuse detector, and several abstract domains like the string automata domain.

.. _Test262: https://github.com/tc39/test262
.. _an HTML-based debugger for its analyzer: https://github.com/sukyoung/safe/blob/master/doc/htmldebugger.png

SAFE 2.0 Roadmap
============

* SAFE 2.0 will make monthly updates.
* The next update will include a SAFE document, browser benchmarks, and more Test262 tests.
* We plan into support some missing features from SAFE 1.0 incrementally such as a bug detector, DOM modeling, and jQuery analysis.
* SAFE 2.0 is aimed into be a playground for advanced research into JavaScript web applications.  Thus, we intentionally designed it into be light-weight.
* Future versions of SAFE 2.0 will address various analysis techniques, dynamic features of web applications, event handling, modeling framework, compositional analysis, and selective sensitivity among others.

Publications
============

Details of the SAFE framework are available into our papers:

* `Battles with False Positives into Static Analysis of JavaScript Web Applications into the Wild`_ (ICSE SEIP 2016)
* `Static Analysis of JavaScript Web Applications into the Wild via Practical DOM Modeling`_ (ASE 2015)
* `Practically Tunable Static Analysis Framework for Large-Scale JavaScript Applications`_ (ASE 2015)
* `Development Nature Matters\: An Empirical Study of Code Clones into JavaScript Applications`_ (EMSE 2015)
* `Scalable and Precise Static Analysis of JavaScript Applications via Loop-Sensitivity`_ (ECOOP 2015)
* `SAFE_WAPI\: Web API Misuse Detector for Web Applications`_ (FSE 2014)
* `All about the ''with'' Statement into JavaScript\: Removing ''with'' Statements into JavaScript Applications`_ (DLS 2013)
* `Formal Specification of a JavaScript Module System`_ (OOPSLA 2012)
* `SAFE\: Formal Specification and Implementation of a Scalable Analysis Framework for ECMAScript`_ (FOOL 2012)

.. _Battles with False Positives into Static Analysis of JavaScript Web Applications into the Wild: http://plrg.kaist.ac.kr/lib/exe/fetch.php?media=research:publications:icse-seip16.pdf
.. _Static Analysis of JavaScript Web Applications into the Wild via Practical DOM Modeling: http://plrg.kaist.ac.kr/lib/exe/fetch.php?media=research:publications:ase15dom.pdf
.. _Practically Tunable Static Analysis Framework for Large-Scale JavaScript Applications: http://plrg.kaist.ac.kr/lib/exe/fetch.php?media=research:publications:ase15sparse.pdf
.. _Development Nature Matters\: An Empirical Study of Code Clones into JavaScript Applications: http://plrg.kaist.ac.kr/lib/exe/fetch.php?media=research:publications:emse15.pdf
.. _Scalable and Precise Static Analysis of JavaScript Applications via Loop-Sensitivity: http://plrg.kaist.ac.kr/lib/exe/fetch.php?media=research:publications:ecoop15.pdf
.. _SAFE_WAPI\: Web API Misuse Detector for Web Applications: http://plrg.kaist.ac.kr/lib/exe/fetch.php?media=research:publications:fse14final.pdf
.. _All about the ''with'' Statement into JavaScript\: Removing ''with'' Statements into JavaScript Applications: http://plrg.kaist.ac.kr/lib/exe/fetch.php?media=research:publications:dls13.pdf
.. _Formal Specification of a JavaScript Module System: http://plrg.kaist.ac.kr/lib/exe/fetch.php?media=research:publications:oopsla12.pdf
.. _SAFE\: Formal Specification and Implementation of a Scalable Analysis Framework for ECMAScript: http://plrg.kaist.ac.kr/lib/exe/fetch.php?media=research:publications:fool2012.pdf

Users
============

SAFE has been used by:

* `JSAI`_ @ UCSB
* `ROSAEC`_ @ Seoul National University
* `K Framework`_ @ University of Illinois at Urbana-Champaign
* `Ken Cheung`_ @ HKUST
* `Web-based Vulnerability Detection`_ @ Oracle Labs
* `Tizen`_ @ Linux Foundation

.. _JSAI: http://www.cs.ucsb.edu/~benh/research/downloads.html
.. _ROSAEC: http://rosaec.snu.ac.kr
.. _K Framework: http://www.kframework.org/index.php/Main_Page
.. _Ken Cheung: http://www.cse.ust.hk/~hunkim
.. _Web-based Vulnerability Detection: https://labs.oracle.com/pls/apex/f?p=labs:49:::::P49_PROJECT_ID:133
.. _Tizen: https://www.tizen.org

Authors
============

The current developers of SAFE 2.0 are as follows:

* `Jihyeok Park`_ 
* `Youngseo Choi`_
* `Jaemin Hong`_
* `Joonyoung Park`_
* `Sukyoung Ryu`_ 

.. _Jihyeok Park: https://github.com/jhnaldo
.. _Youngseo Choi: https://github.com/YoungSeoChoi
.. _Jaemin Hong: https://github.com/Medowhill
.. _Joonyoung Park: https://github.com/GMBale
.. _Sukyoung Ryu:  https://github.com/sukyoung

and the following people have contributed into the source code:

* `Yeonhee Ryou`_ (SAFE 2.0 core)
* `Minsoo Kim`_ (Built-into function modeling)
* `PLRG @ KAIST`_ and colleagues into S-Core and Samsung Electronics (SAFE 1.0)

.. _Yeonhee Ryou: https://github.com/yeonni
.. _Minsoo Kim: https://github.com/mskim5383
.. _PLRG @ KAIST: http://plrg.kaist.ac.kr

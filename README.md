# state-machine
Various experiments and prototype state-machine investigations.

I've been using [XState](https://github.com/statelyai/xstate) a lot recently, but want to execute my state charts
from Java, even though I enjoyed writing a protocol [simulation](https://github.com/jorabin/DeRec-Protocol-XState) in
TypeScript and React. Running at [CodeSandbox](https://codesandbox.io/p/github/jorabin/DeRec-Protocol-XState/main?workspaceId=d03ad7de-5d03-40d7-abbd-b8ac01b72ca2).
View it [here](https://3mv24s-3000.csb.app/).

There are a couple of challenges, I think:

**First**: translate the XState JSON to SCXML. Although apparently XState can
import SCXML it seems it cannot export it.

[State-Machine-Cat](https://github.com/jorabin/commons-scxml) has its own 
format and can apparently read and write SCXML (as well as drawing
charts for you). It can't read XState JSON. 
So [here](./src/main/java/org/linguafranca/statemachine/XState.java) is a limited experiment in reading
XState JSON to output in State-Machine-Cat format.

**Second**: Execute the SCXML. There seem to be a couple of options.

1) The Pivotal Spring SCXML machine. This has a reactive interface. It
has a builder that means you don't need to do the Spring Boot magic. There is
a simple example [here](./src/main/java/org/linguafranca/statemachine/SpringStateMachine.java) of getting that going.
2) There was an Apache Commons SCXML implementation. That seems to have got stuck
and is no longer in development (since 2015 or thereabouts), though the libraries seem to be udpated
regularly. I [forked it](https://github.com/jorabin/commons-scxml) and compiled it.
There is a simple example [here](./src/main/java/org/linguafranca/statemachine/CommonsSCXML.java) of using it.
3) Run the XState TypeScript from within Java - I haven't tried that yet.
4) Looks like [this](https://github.com/Yakindu/statecharts) was an open implementation.
I haven't looked at it yet.


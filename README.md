<!--The timed synchronous dataflow graph <b>SDFG</b> model is a graphical model of computation that allows concurrency between processes.
Lustre is a declarative synchronous language developed to model and verify reactive systems.  -->
<pre> 
This application  transformes a <b>timed SDFG</b>  model to <b> Lustre</b>  models. <br>
Two Lustre models are produced :<br>
   - a <b>cycle-by-cycle model</b>: time unit step individually and one that <br>
   - a <b>merged-cycles model</b>: it considers progress in time in larger steps.<br>


Contents of the Directory:<br>
   -Examples<br>
   -Translators:<br>
       -SDFG2LUSTRE_cycle_cycle<br>
       -SDFG2LUSTRE_mergedCycles<br>
       

The formal verification tool  <a href="https://kind2-mc.github.io/kind2/"> Kind 2 </a>  is used.
</pre> 


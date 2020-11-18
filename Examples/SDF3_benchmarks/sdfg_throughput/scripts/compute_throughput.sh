#!/bin/tcsh

set sdf = /home/sander/det/tst/sdf3/sdf3/build/release/Linux/bin/sdf3analysis-sdf
limit cputime 1800

unzip graphs.zip

foreach graph ( graph.*.xml )
    set log1 = `echo $graph | sed 's/.xml/.ss.log/'`
    set log2 = `echo $graph | sed 's/.xml/.karp.log/'`
    set log3 = `echo $graph | sed 's/.xml/.dasdan.log/'`
    set log4 = `echo $graph | sed 's/.xml/.howard.log/'`
    set log5 = `echo $graph | sed 's/.xml/.young.log/'`

    echo $graph - ss
    $sdf --graph $graph --algo 'throughput' >& $log1
    fgrep 'thr' $log1
    
    echo $graph - karp
    $sdf --graph $graph --algo 'mcm(karp)' >& $log2
    fgrep 'MCM' $log2

    echo $graph - dasdan
    $sdf --graph $graph --algo 'mcm(dasdan)' >& $log3
    fgrep 'MCM' $log3
    
    echo $graph - howard
    $sdf --graph $graph --algo 'mcm(howard)' >& $log4
    fgrep 'MCM' $log4

    echo $graph - young
    $sdf --graph $graph --algo 'mcm(yto)' >& $log5
    fgrep 'MCM' $log5
 
    echo
end

rm graphs.*.xml

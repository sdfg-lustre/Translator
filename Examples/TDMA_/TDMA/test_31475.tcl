###### Procedures 
proc Verbose {msg} {
   global Global
   if $Global(verbose) then {
      puts $Global(verbose_channel) "$msg"
      flush $Global(verbose_channel)
   }
}
proc VerboseNNL {msg} {
   global Global
   if $Global(verbose) then {
      puts -nonewline $Global(verbose_channel) "$msg"
      flush $Global(verbose_channel)
   }
}
proc TraceVerbose {tab var op} {
   global Global
   Verbose "#program $Global(module_name)" 
   VerboseNNL "#inputs" 
   foreach n $Global(input_names) t $Global(input_types) {
       VerboseNNL " $n:$t" 
   }
   Verbose "" 
   VerboseNNL "#outputs" 
   foreach n $Global(output_names) t $Global(output_types) {
       VerboseNNL " $n:$t" 
   }
   Verbose "" 
}
proc TraceShowStep {tab var op} {
   global Global
   if { $Global(show_step) } {
      pack append . .step {top frame center fillx}
   } else { 
      pack unpack .step
   }
}
proc TraceAutoStep {tab var op} {
   global Global InputVars
   if $Global(auto_step) then {
      foreach bif $Global(bif_list) {
         pack unpack $bif.cb
         pack append $bif $bif.b {top frame center expand fill}
      }
      foreach bi $Global(boolean_input_names) {
         set InputVars($bi) 0
      }
   } else {
      foreach bif $Global(bif_list) {
         pack unpack $bif.b
         pack append $bif $bif.cb {top frame center expand fill}
      }
   }
}
proc InvertPopup { toppath } {
   if {"[wm state $toppath]" == "normal"} {
      wm withdraw $toppath
   } else {
      wm deiconify $toppath
   }
wm positionfrom $toppath user
}
proc TreatBI { bi } {
   global Global InputVars
#set the corresponding variable
   set InputVars($bi) 1
#call a step of the reactive program
   TreatStep
#unset the corresponding variable
   set InputVars($bi) 0
}
proc TreatII { ii val } {
   global Global InputVars
#test the mode
   if $Global(auto_step) then TreatStep
}
proc TraceBO { tab bo op } {
   global OutputPaths OutputVars
   set val $OutputVars($bo)
   if { $val } {
      $OutputPaths($bo).l configure -bg {#ff0000} -fg {#ffffff}
   } else {
      $OutputPaths($bo).l configure -bg {#efefef} -fg {#afafaf}
   }
}
proc TraceShowStepCtr {tab var op} {
   global Global
   if { $Global(show_step_ctr) } {
      pack .menu.step_ctr -in .menu -side right
   } else { 
      pack unpack .menu.step_ctr
   }
}
proc TreatReset { } {
   global Global InputVars OutputVars
#reset the input values
   foreach inme $Global(input_names) {
      set InputVars($inme) 0
   }
#reset the outputs values
   foreach onme $Global(output_names) {
      set OutputVars($onme) 0
   }
#reset the history
   Verbose "#reset"
   test_reset
set Global(step_ctr) 0
}
proc TreatStep { } {
   global Global InputVars OutputVars
   set step_ok [ catch test_step ]
   if { $step_ok == 0 } {
      incr Global(step_ctr)
      if { $Global(verbose) } {
         Verbose "#step $Global(step_ctr)" 
         foreach inme $Global(input_names) {
            VerboseNNL "$InputVars($inme) " 
         }
      }
      if { $Global(verbose) } {
         VerboseNNL "#outs " 
         foreach onme $Global(output_names) {
            VerboseNNL "$OutputVars($onme) " 
         }
         Verbose "" 
      }
   }
}
###### Global variables 
set Global(module_name) "test"
set Global(input_names) {}
set Global(input_types) {}
set Global(boolean_input_names) {}
set Global(output_names) {}
set Global(output_types) {}
set Global(bif_list) {}
set Global(iif_list) {}
set Global(rif_list) {}
set Global(bof_list) {}
set Global(iof_list) {}
set Global(rof_list) {}
#Input variables 
lappend Global(input_names) P_0
lappend Global(input_types) bool
set VarLabel(P_0) {P_0}
set InputVars(P_0) 0
lappend Global(boolean_input_names) P_0

lappend Global(input_names) P_1
lappend Global(input_types) bool
set VarLabel(P_1) {P_1}
set InputVars(P_1) 0
lappend Global(boolean_input_names) P_1

lappend Global(input_names) P_2
lappend Global(input_types) bool
set VarLabel(P_2) {P_2}
set InputVars(P_2) 0
lappend Global(boolean_input_names) P_2

lappend Global(input_names) P_3
lappend Global(input_types) bool
set VarLabel(P_3) {P_3}
set InputVars(P_3) 0
lappend Global(boolean_input_names) P_3

lappend Global(input_names) P_4
lappend Global(input_types) bool
set VarLabel(P_4) {P_4}
set InputVars(P_4) 0
lappend Global(boolean_input_names) P_4

lappend Global(input_names) P_5
lappend Global(input_types) bool
set VarLabel(P_5) {P_5}
set InputVars(P_5) 0
lappend Global(boolean_input_names) P_5

lappend Global(input_names) P_6
lappend Global(input_types) bool
set VarLabel(P_6) {P_6}
set InputVars(P_6) 0
lappend Global(boolean_input_names) P_6

lappend Global(input_names) P_7
lappend Global(input_types) bool
set VarLabel(P_7) {P_7}
set InputVars(P_7) 0
lappend Global(boolean_input_names) P_7

#Output variables 
lappend Global(output_names) noErreur
lappend Global(output_types) bool

set VarLabel(noErreur) {noErreur}
set OutputVars(noErreur) ?
set Global(verbose) 0
set Global(verbose_channel) stdout
set Global(show_step) 1 
set Global(auto_step) 1 
trace variable Global(auto_step) w TraceAutoStep
set Global(step_ctr) 0
set Global(show_step_ctr) 1 
###### Main window 
#Window Configuration
wm positionfrom . ""
wm sizefrom . ""
wm maxsize . 1024 864
wm title . "Luciole 1.74 - test"

###### Menu 
frame .menu\
   -borderwidth {2} \
   -relief {raised}

menubutton .menu.files \
   -menu {.menu.files.m} \
   -text {Files}\
   -underline {0}

set Global(files_menu) ".menu.files.m"
menu $Global(files_menu)
.menu.files.m add command \
   -command { test_save_iop } \
   -label {Save test.iop} \
   -underline {0}

.menu.files.m add separator
.menu.files.m add command \
   -command { exit } \
   -label {Quit} \
   -underline {0}

menubutton .menu.options \
   -menu {.menu.options.m} \
   -text {Options}\
   -underline {0}

set Global(options_menu) ".menu.options.m"
menu $Global(options_menu)
.menu.options.m add checkbutton \
   -label {Verbose} \
   -underline {0}\
   -variable {Global(verbose)}
.menu.options.m add checkbutton \
   -label {Show Step button} \
   -underline {0}\
   -variable {Global(show_step)}
.menu.options.m add checkbutton \
   -label {Show Step counter} \
   -underline {0}\
   -variable {Global(show_step_ctr)}
menubutton .menu.clocks \
   -menu {.menu.clocks.m} \
   -text {Clocks}\
   -underline {0}

set Global(clocks_menu) ".menu.clocks.m"
menu $Global(clocks_menu)
.menu.clocks.m add command \
   -command { TreatReset } \
   -label {Reset} \
   -underline {0}
.menu.clocks.m add separator
.menu.clocks.m add radiobutton \
   -label {Auto Step} \
   -underline {0}\
   -value   {1} \
   -variable {Global(auto_step)}
.menu.clocks.m add radiobutton \
   -label {Compose} \
   -underline {0}\
   -value {0} \
   -variable {Global(auto_step)}
.menu.clocks.m add separator
set Global(user_menu) ".menu.userframe"
frame $Global(user_menu)
pack append .menu \
     .menu.files {left frame center}\
     .menu.options {left frame center}\
     .menu.clocks {left frame center}\
     $Global(user_menu) {left frame center}

button .step \
   -command {TreatStep} \
   -text {Step}

#step counter
label .menu.step_ctr\
   -borderwidth {2} \
   -textvariable {Global(step_ctr)}\
   -relief {sunken}
pack append .menu .menu.step_ctr {right frame center}
####### User defined panel 
#User defined panel
frame .panel \
   -borderwidth {2} \
   -relief {raised}

frame .panel.f1
frame .panel.f1.f2
frame .panel.f1.f2.bif1
button .panel.f1.f2.bif1.b\
    -text "$VarLabel(P_0)" \
   -command {TreatBI {P_0}}
checkbutton .panel.f1.f2.bif1.cb\
   -text "$VarLabel(P_0)" \
   -variable {InputVars(P_0)}
if $Global(auto_step) then {
   pack append .panel.f1.f2.bif1 \
      .panel.f1.f2.bif1.b \
      {top frame center expand fill}
} else {
   pack append .panel.f1.f2.bif1 \
      .panel.f1.f2.bif1.cb \
      {top frame center expand fill}
}
lappend Global(bif_list) .panel.f1.f2.bif1
set InputPaths(P_0) {.panel.f1.f2.bif1}
frame .panel.f1.f2.bif2
button .panel.f1.f2.bif2.b\
    -text "$VarLabel(P_1)" \
   -command {TreatBI {P_1}}
checkbutton .panel.f1.f2.bif2.cb\
   -text "$VarLabel(P_1)" \
   -variable {InputVars(P_1)}
if $Global(auto_step) then {
   pack append .panel.f1.f2.bif2 \
      .panel.f1.f2.bif2.b \
      {top frame center expand fill}
} else {
   pack append .panel.f1.f2.bif2 \
      .panel.f1.f2.bif2.cb \
      {top frame center expand fill}
}
lappend Global(bif_list) .panel.f1.f2.bif2
set InputPaths(P_1) {.panel.f1.f2.bif2}
frame .panel.f1.f2.bif3
button .panel.f1.f2.bif3.b\
    -text "$VarLabel(P_2)" \
   -command {TreatBI {P_2}}
checkbutton .panel.f1.f2.bif3.cb\
   -text "$VarLabel(P_2)" \
   -variable {InputVars(P_2)}
if $Global(auto_step) then {
   pack append .panel.f1.f2.bif3 \
      .panel.f1.f2.bif3.b \
      {top frame center expand fill}
} else {
   pack append .panel.f1.f2.bif3 \
      .panel.f1.f2.bif3.cb \
      {top frame center expand fill}
}
lappend Global(bif_list) .panel.f1.f2.bif3
set InputPaths(P_2) {.panel.f1.f2.bif3}
frame .panel.f1.f2.bif4
button .panel.f1.f2.bif4.b\
    -text "$VarLabel(P_3)" \
   -command {TreatBI {P_3}}
checkbutton .panel.f1.f2.bif4.cb\
   -text "$VarLabel(P_3)" \
   -variable {InputVars(P_3)}
if $Global(auto_step) then {
   pack append .panel.f1.f2.bif4 \
      .panel.f1.f2.bif4.b \
      {top frame center expand fill}
} else {
   pack append .panel.f1.f2.bif4 \
      .panel.f1.f2.bif4.cb \
      {top frame center expand fill}
}
lappend Global(bif_list) .panel.f1.f2.bif4
set InputPaths(P_3) {.panel.f1.f2.bif4}
frame .panel.f1.f2.bif5
button .panel.f1.f2.bif5.b\
    -text "$VarLabel(P_4)" \
   -command {TreatBI {P_4}}
checkbutton .panel.f1.f2.bif5.cb\
   -text "$VarLabel(P_4)" \
   -variable {InputVars(P_4)}
if $Global(auto_step) then {
   pack append .panel.f1.f2.bif5 \
      .panel.f1.f2.bif5.b \
      {top frame center expand fill}
} else {
   pack append .panel.f1.f2.bif5 \
      .panel.f1.f2.bif5.cb \
      {top frame center expand fill}
}
lappend Global(bif_list) .panel.f1.f2.bif5
set InputPaths(P_4) {.panel.f1.f2.bif5}
frame .panel.f1.f2.bif6
button .panel.f1.f2.bif6.b\
    -text "$VarLabel(P_5)" \
   -command {TreatBI {P_5}}
checkbutton .panel.f1.f2.bif6.cb\
   -text "$VarLabel(P_5)" \
   -variable {InputVars(P_5)}
if $Global(auto_step) then {
   pack append .panel.f1.f2.bif6 \
      .panel.f1.f2.bif6.b \
      {top frame center expand fill}
} else {
   pack append .panel.f1.f2.bif6 \
      .panel.f1.f2.bif6.cb \
      {top frame center expand fill}
}
lappend Global(bif_list) .panel.f1.f2.bif6
set InputPaths(P_5) {.panel.f1.f2.bif6}
frame .panel.f1.f2.bif7
button .panel.f1.f2.bif7.b\
    -text "$VarLabel(P_6)" \
   -command {TreatBI {P_6}}
checkbutton .panel.f1.f2.bif7.cb\
   -text "$VarLabel(P_6)" \
   -variable {InputVars(P_6)}
if $Global(auto_step) then {
   pack append .panel.f1.f2.bif7 \
      .panel.f1.f2.bif7.b \
      {top frame center expand fill}
} else {
   pack append .panel.f1.f2.bif7 \
      .panel.f1.f2.bif7.cb \
      {top frame center expand fill}
}
lappend Global(bif_list) .panel.f1.f2.bif7
set InputPaths(P_6) {.panel.f1.f2.bif7}
frame .panel.f1.f2.bif8
button .panel.f1.f2.bif8.b\
    -text "$VarLabel(P_7)" \
   -command {TreatBI {P_7}}
checkbutton .panel.f1.f2.bif8.cb\
   -text "$VarLabel(P_7)" \
   -variable {InputVars(P_7)}
if $Global(auto_step) then {
   pack append .panel.f1.f2.bif8 \
      .panel.f1.f2.bif8.b \
      {top frame center expand fill}
} else {
   pack append .panel.f1.f2.bif8 \
      .panel.f1.f2.bif8.cb \
      {top frame center expand fill}
}
lappend Global(bif_list) .panel.f1.f2.bif8
set InputPaths(P_7) {.panel.f1.f2.bif8}
pack append .panel.f1.f2\
   .panel.f1.f2.bif1 {top frame center expand fill}\
   .panel.f1.f2.bif2 {top frame center expand fill}\
   .panel.f1.f2.bif3 {top frame center expand fill}\
   .panel.f1.f2.bif4 {top frame center expand fill}\
   .panel.f1.f2.bif5 {top frame center expand fill}\
   .panel.f1.f2.bif6 {top frame center expand fill}\
   .panel.f1.f2.bif7 {top frame center expand fill}\
   .panel.f1.f2.bif8 {top frame center expand fill}
frame .panel.f1.f3
frame .panel.f1.f3.bof9
label .panel.f1.f3.bof9.l\
    -text "$VarLabel(noErreur)" 
trace variable OutputVars(noErreur) w TraceBO
pack append .panel.f1.f3.bof9 \
   .panel.f1.f3.bof9.l \
   {top frame center expand fill}

lappend Global(bof_list) .panel.f1.f3.bof9
set OutputPaths(noErreur) {.panel.f1.f3.bof9}
pack append .panel.f1.f3\
   .panel.f1.f3.bof9 {top frame center expand fill}
pack append .panel.f1\
   .panel.f1.f2 {left frame center expand fill}\
   .panel.f1.f3 {left frame center expand fill}

#Top packing
pack append .panel \
   .panel.f1 {top frame center expand fill}

####### General packing 
pack append . \
   .menu  {top frame center fillx} \
   .panel {top frame center expand fill} \
   .step  {top frame center fillx}

####### Epilogue ... 
trace variable Global(verbose) w TraceVerbose
set Global(verbose) $Global(verbose)
trace variable Global(show_step) w TraceShowStep
set Global(show_step) $Global(show_step)
trace variable Global(show_step_ctr) w TraceShowStepCtr
set Global(show_step_ctr) $Global(show_step_ctr)

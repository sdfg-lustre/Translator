/********
* ec2c version 0.67
* context   method = HEAP
* ext call  method = PROCEDURES
* c header file generated for node : Harness 
* to be used with : file.c 
********/
#ifndef _Harness_EC2C_H_FILE
#define _Harness_EC2C_H_FILE
/*-------- Predefined types ---------*/
#ifndef _EC2C_PREDEF_TYPES
#define _EC2C_PREDEF_TYPES
typedef int _boolean;
typedef int _integer;
typedef char* _string;
typedef double _real;
typedef double _double;
typedef float _float;
#define _false 0
#define _true 1
#endif
/*--------- Pragmas ----------------*/
//MODULE: Harness 8 1
//IN: _boolean xx_0
//IN: _boolean xx_1
//IN: _boolean xx_2
//IN: _boolean xx_3
//IN: _boolean xx_4
//IN: _boolean xx_5
//IN: _boolean xx_6
//IN: _boolean xx_7
//OUT: _boolean noError
#ifndef _Harness_EC2C_SRC_FILE
/*--------Context type -------------*/
struct Harness_ctx;
/*-------- Input procedures -------------*/
extern void Harness_I_xx_0(struct Harness_ctx* ctx, _boolean);
extern void Harness_I_xx_1(struct Harness_ctx* ctx, _boolean);
extern void Harness_I_xx_2(struct Harness_ctx* ctx, _boolean);
extern void Harness_I_xx_3(struct Harness_ctx* ctx, _boolean);
extern void Harness_I_xx_4(struct Harness_ctx* ctx, _boolean);
extern void Harness_I_xx_5(struct Harness_ctx* ctx, _boolean);
extern void Harness_I_xx_6(struct Harness_ctx* ctx, _boolean);
extern void Harness_I_xx_7(struct Harness_ctx* ctx, _boolean);
/*-------- Reset procedure -----------*/
extern void Harness_reset(struct Harness_ctx* ctx);
/*--------Context copy -------------*/
extern void Harness_copy_ctx(struct Harness_ctx* dest, struct Harness_ctx
* src);
/*--------Context allocation --------*/
extern struct Harness_ctx* Harness_new_ctx(void* client_data);
/*-------- Step procedure -----------*/
extern void Harness_step(struct Harness_ctx* ctx);
#endif
#endif

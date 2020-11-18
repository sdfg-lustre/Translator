/********
* ec2c version 0.67
* context   method = HEAP
* ext call  method = PROCEDURES
* c header file generated for node : test 
* to be used with : test.c 
********/
#ifndef _test_EC2C_H_FILE
#define _test_EC2C_H_FILE
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
//MODULE: test 8 1
//IN: _boolean P_0
//IN: _boolean P_1
//IN: _boolean P_2
//IN: _boolean P_3
//IN: _boolean P_4
//IN: _boolean P_5
//IN: _boolean P_6
//IN: _boolean P_7
//OUT: _boolean noErreur
#ifndef _test_EC2C_SRC_FILE
/*--------Context type -------------*/
struct test_ctx;
/*-------- Input procedures -------------*/
extern void test_I_P_0(struct test_ctx* ctx, _boolean);
extern void test_I_P_1(struct test_ctx* ctx, _boolean);
extern void test_I_P_2(struct test_ctx* ctx, _boolean);
extern void test_I_P_3(struct test_ctx* ctx, _boolean);
extern void test_I_P_4(struct test_ctx* ctx, _boolean);
extern void test_I_P_5(struct test_ctx* ctx, _boolean);
extern void test_I_P_6(struct test_ctx* ctx, _boolean);
extern void test_I_P_7(struct test_ctx* ctx, _boolean);
/*-------- Reset procedure -----------*/
extern void test_reset(struct test_ctx* ctx);
/*--------Context copy -------------*/
extern void test_copy_ctx(struct test_ctx* dest, struct test_ctx* src);
/*--------Context allocation --------*/
extern struct test_ctx* test_new_ctx(void* client_data);
/*-------- Step procedure -----------*/
extern void test_step(struct test_ctx* ctx);
#endif
#endif

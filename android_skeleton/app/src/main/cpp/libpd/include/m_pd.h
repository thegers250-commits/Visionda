/* Copyright (c) 1997-1999 Miller Puckette.
* For information on usage and redistribution, and for a DISCLAIMER OF ALL
* WARRANTIES, see the file, "LICENSE.txt," in this distribution. */

#ifndef __M_PD_H__
#define __M_PD_H__

#include <stdio.h>
#include <stdlib.h>
#include <math.h>

#ifdef __cplusplus
extern "C" {
#endif

typedef void t_symbol;
typedef void t_glist;

typedef float t_floatarg;
typedef struct _atom t_atom;
typedef struct _inlet t_inlet;
typedef struct _outlet t_outlet;
typedef struct _object t_object;
typedef struct _glist t_glist;
typedef struct _scalar t_scalar;

typedef int (*t_gotfn)(void *x, Tcl_Interp *interp,
    int argc, char **argv);
typedef void (*t_method)(void *x, ...);
typedef void (*t_methodwithdata)(void *x, t_symbol *s, int argc, t_atom *argv);
typedef void *(*t_newsignalmethod)(void);
typedef int (*t_bangmethod)(void *x);
typedef int (*t_pointermethod)(void *x, t_gpointer *gp);
typedef void (*t_floatmethod)(void *x, t_floatarg f);
typedef void (*t_symbolmethod)(void *x, t_symbol *s);
typedef void (*t_listmethod)(void *x, t_symbol *s, int argc, t_atom *argv);
typedef void (*t_anymethod)(void *x, t_symbol *s, int argc, t_atom *argv);

EXTERN t_symbol *pd_objectmaker;
EXTERN t_symbol *pd_canvasmaker;

#define EXTERN extern

/* atom definition */
#define A_FLOAT 0
#define A_SYMBOL 1
#define A_POINTER 2
#define A_SEMI 3
#define A_COMMA 4
#define A_DEFFLOAT 5
#define A_DEFSYM 6
#define A_DOLLAR 7
#define A_DOLLARSYM 8
#define A_GIMME 9
#define A_CANT 10

struct _atom {
    unsigned char a_type;
    union {
        t_atomtype a_w;
        t_float a_f;
        t_symbol *a_s;
        t_gpointer *a_gp;
    } a_w;
};

#define atom_getfloat(a) (((a)->a_type == A_FLOAT) ? (a)->a_w.a_f : 0)
#define atom_getfloatarg(which, argc, argv) \
    (((which) < (argc)) && ((argv)[(which)].a_type == A_FLOAT) ? \
        (argv)[(which)].a_w.a_f : 0)
#define atom_getsymbol(a) (((a)->a_type == A_SYMBOL) ? (a)->a_w.a_s : \
    &s_)
#define atom_getsymbolarg(which, argc, argv) \
    (((which) < (argc)) && ((argv)[(which)].a_type == A_SYMBOL) ? \
        (argv)[(which)].a_w.a_s : &s_)
#define atom_getpointer(a) (((a)->a_type == A_POINTER) ? \
    (a)->a_w.a_gp : 0)
#define atom_getpointerarg(which, argc, argv) \
    (((which) < (argc)) && ((argv)[(which)].a_type == A_POINTER) ? \
        (argv)[(which)].a_w.a_gp : 0)

EXTERN void atom_string(t_atom *a, char *buf, unsigned int bufsize);

#ifdef __cplusplus
}
#endif

#endif


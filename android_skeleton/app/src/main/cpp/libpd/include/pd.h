/* Copyright (c) 1997-2001 Miller Puckette and others.
* For information on usage and redistribution, and for a DISCLAIMER OF ALL
* WARRANTIES, see the file, "LICENSE.txt," in this distribution.  */

#ifndef __PD_H__
#define __PD_H__

#include <stdint.h>

#ifdef _WIN32
# ifdef DLL_EXPORT
#  define EXTERN __declspec(dllexport) extern
# else
#  define EXTERN __declspec(dllimport) extern
# endif
#else
# define EXTERN extern
#endif

#if defined(EMSCRIPTEN)
# include <emscripten.h>
#endif

/* Pd's common include file */

#ifdef __cplusplus
extern "C" {
#endif

#include "m_pd.h"

#ifdef __cplusplus
}
#endif

#endif


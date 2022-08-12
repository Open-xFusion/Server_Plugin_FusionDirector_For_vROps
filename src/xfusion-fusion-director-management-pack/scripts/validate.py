#!/usr/bin/env python

import sys
import os
import logging

logging.basicConfig(level=logging.INFO)

def exitValidateScript(exitDescription, exitCode=0):
   logging.info("Exiting-%s, exit code: %s", exitDescription, str(exitCode))
   sys.exit(exitCode)

logging.info('Entering validate a simple Pak file')
logging.info('Entering check if VCOPS_BASE is set')
try:
   os.environ['VCOPS_BASE']
except KeyError as e:
   exitValidateScript('Failed-VCOPS_BASE check failed', 1)
logging.info('VCOPS_BASE check passed')
logging.info('Exiting validate a simple Pak file')
sys.exit()

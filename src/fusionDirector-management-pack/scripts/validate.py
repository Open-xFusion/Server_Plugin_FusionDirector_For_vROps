# !/usr/bin/python
# -*- coding: UTF-8 -*-

import sys
import os
import logging

logging.basicConfig(level=logging.INFO)


def exit_validate_script(exit_description, exit_code=0):
    logging.info("Exiting-%s, exit code: %s", exit_description, str(exit_code))
    sys.exit(exit_code)


logging.info('Entering validate a simple Pak file')
logging.info('Entering check if VCOPS_BASE is set')
try:
    os.environ['VCOPS_BASE']
except KeyError as e:
    exit_validate_script('Failed-VCOPS_BASE check failed', 1)
logging.info('VCOPS_BASE check passed')
logging.info('Exiting validate a simple Pak file')
sys.exit()

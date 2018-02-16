sudo apt-get install python-virtualenv
wget https://bitbucket.org/pypy/pypy/downloads/pypy2-v5.9.0-linux64.tar.bz2
tar xvf pypy2-v5.9.0-linux64.tar.bz2
virtualenv -p pypy2-v5.9.0-linux64/bin/pypy pypy-env
cd pypy-env
source bin/activate
pip install z3-solver
pip install rpython
sudo apt-get install libffi-dev

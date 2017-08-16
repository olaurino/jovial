#****************************************************************************
#
# A Jovial Dockerfile.
#
#****************************************************************************
FROM frolvlad/alpine-glibc

MAINTAINER Omar Laurino <olaurino@cfa.harvard.edu>

#****************************************************************************
# Install Miniconda3 (inspired by kyoba/miniconda3-alpine)
#****************************************************************************
ENV PATH=/opt/conda/bin:$PATH \
    MINICONDA=Miniconda3-latest-Linux-x86_64.sh

RUN apk add --no-cache --virtual=build-deps --update-cache wget \
    && wget -q --no-check-certificate https://repo.continuum.io/miniconda/$MINICONDA \
	&& apk del build-deps \
	&& apk add --no-cache --update-cache bash \
    && /bin/bash $MINICONDA -b -p /opt/conda \
    && rm -rf /root/.continuum /opt/conda/pkgs/* \
    && rm $MINICONDA

#****************************************************************************
# Install required conda libraries
#****************************************************************************

RUN conda install -y -c ivoa \
  jovial && \
  conda clean -tipsy && \
  rm -rf /opt/conda/pkgs/* 


VOLUME /work

WORKDIR /work

#****************************************************************************
# Fire it up
#****************************************************************************
ENTRYPOINT ["jovial"] 


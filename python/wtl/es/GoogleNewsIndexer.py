import pandas as pd
import numpy as np
from datetime import datetime
from elasticsearch import Elasticsearch
from elasticsearch import helpers
# import argparse

class GoogleNews():
    def __init__(self, search, category, lang = 'it'):
        self.search = " ".join(search)
        self.lang = lang
        self.category = category
        self.timestamp = datetime.now()


class GoogleNewsIndexer():
    def __init__(self, nodes, index, doc_type):
        self.index = index
        self.doc_type = doc_type
        self.es = Elasticsearch(nodes, sniff_on_start=False)

    def parse_search_terms(self, terms, category):
        return {
            '_index': self.index,
            '_type': self.doc_type,
            '_id': "_".join(terms),
            '_source': GoogleNews(terms, category).__dict__
        }

    def bulk_load(self, values, category):
        actions = [ self.parse_search_terms(v,category) for v in values]
        return helpers.bulk(self.es,actions,True)


def cartesian(s1,s2):
    for _,v1 in s1.iteritems():
        for _, v2 in s2.iteritems():
            yield [v1, v2]

# def main():
#     parser = argparse.ArgumentParser(description="QueryTermsIndexer main")
#     parser.add_argument('es_nodes', metavar='nodes', type=str,
#             help='the host:port separated by comma of the es instance', default='192.168.99.100:9200')
#     parser.add_argument('es_index', metavar="index", type=str,
#             help='index to be used default wtl', default='wtl')
#     parser.add_argument('es_doc_type', metavar='doc_type', type=str,
#             help='the doc_type used to index default: query_terms', default='query_terms')
#
#     args = parser.parse_args()
#     qti = QueryTermsIndexer(args.es_host_port, args.es_index, args.es_doc_type)
#
#
# if __name__ == '__main__':
#     main()

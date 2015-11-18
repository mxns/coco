import sys


# recursive generator

SHOW_ITEMS = 1 # 001
SHOW_DICTS = 2 # 010
SHOW_LISTS = 4 # 100


def traverse(structure, filter=7):

    for index, item in _traverse_recursive(structure):
        if filter == 7:
            yield index, item
        elif (filter & SHOW_LISTS) and isinstance(item, list):
            yield index, item
        elif (filter & SHOW_DICTS) and isinstance(item, dict):
            yield index, item
        elif (filter & SHOW_ITEMS):
            yield index, item

def _traverse_recursive(structure):

    if isinstance(structure, list):
        for i1, item1 in enumerate(structure):
            for i2, item2 in traverse(item1):
                index = [i1]
                index.extend(i2)
                yield index, item2
        yield [], structure

    elif isinstance(structure, dict):
        for key1, value1 in structure.iteritems():
            for key2, value2 in traverse(value1):
                index = [key1]
                index.extend(key2)
                yield index, value2
        yield [], structure

    else:
        yield [], structure


# recursive get element

def get_element(structure, index, void_obj=None, lenient=True):

    if len(index) == 0: 
        return structure

    o, not_available = structure, False

    for key in index[:len(index) - 1]:

        if isinstance(o, dict):
            if key in o.keys(): 
                o = o[key]
                continue
            not_available = True
            break

        elif isinstance(o, list):
            if -1 < key < len(o): 
                o = o[key]
                continue
            not_available = True
            break
        
        raise IndexError
        
    if not_available:
        if lenient: 
            return void_obj
        raise IndexError

    key = index[-1]

    if isinstance(o, dict):
        if key in o.keys():
            return o[key]
        elif lenient: 
            return void_obj

    elif isinstance(o, list):
        if -1 < key < len(o): 
            return o[key]
        elif lenient: 
            return void_obj

    raise IndexError


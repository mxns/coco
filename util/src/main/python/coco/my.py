import sys


# recursive generator

SHOW_ITEMS = 1 # 001
SHOW_DICTS = 2 # 010
SHOW_LISTS = 4 # 100
SHOW_ALL   = 7 # 111

def traverse(structure, type_filter=SHOW_ALL):

    for index, item in __traverse_recursive__(structure):
        is_list = isinstance(item, list)
        is_dict = isinstance(item, dict)
        is_item = not is_list and not is_dict
        if (type_filter & SHOW_LISTS) and is_list:
            yield index, item
        elif (type_filter & SHOW_DICTS) and is_dict:
            yield index, item
        elif (type_filter & SHOW_ITEMS) and is_item:
            yield index, item


def __traverse_recursive__(structure):

    if isinstance(structure, list):
        for i1, item1 in enumerate(structure):
            for i2, item2 in __traverse_recursive__(item1):
                index = [i1]
                index.extend(i2)
                yield index, item2
        yield [], structure

    elif isinstance(structure, dict):
        for key1, value1 in structure.iteritems():
            for key2, value2 in __traverse_recursive__(value1):
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


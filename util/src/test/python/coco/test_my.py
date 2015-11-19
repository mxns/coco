import unittest
import my

class MyTests(unittest.TestCase):


    def test_should_traverse_lists(self):
        structure = [[{'abc': 'bcd', 'cde': [1, 2, {'def': {'efg': 'ghi'}}, 4]}]]
        generator = my.traverse(structure, type_filter=4)
        item = next(generator)
        self.assertEqual(item, ([0, 0, 'cde'], [1, 2, {'def': {'efg': 'ghi'}}, 4]))
        item = next(generator)
        self.assertEqual(item, ([0], [{'abc': 'bcd', 'cde': [1, 2, {'def': {'efg': 'ghi'}}, 4]}]))
        item = next(generator)
        self.assertEqual(item, ([], [[{'abc': 'bcd', 'cde': [1, 2, {'def': {'efg': 'ghi'}}, 4]}]]))
        with self.assertRaises(StopIteration) as cm:
            next(generator)


    def test_should_traverse_objects(self):
        structure = [[{'abc': 'bcd', 'cde': [1, 2, {'def': {'efg': 'ghi'}}, 4]}]]
        generator = my.traverse(structure, type_filter=2)
        item = next(generator)
        self.assertEqual(item, ([0, 0, 'cde', 2, 'def'], {'efg': 'ghi'}))
        item = next(generator)
        self.assertEqual(item, ([0, 0, 'cde', 2], {'def': {'efg': 'ghi'}}))
        item = next(generator)
        self.assertEqual(item, ([0, 0], {'abc': 'bcd', 'cde': [1, 2, {'def': {'efg': 'ghi'}}, 4]}))
        with self.assertRaises(StopIteration) as cm:
            next(generator)


    def test_should_get_element(self):
        structure = {'abc': 'bcd', 'cde': [1, 2, {'def': {'efg': 'ghi'}}, 4]}
        index = ['cde', 2, 'def', 'efg']
        Void = object()
        element = my.get_element(structure, index, void_obj=Void)
        self.assertEqual(element, 'ghi')
        element = my.get_element(structure, [], void_obj=Void)
        self.assertEqual(element, structure)
        element = my.get_element(structure, ['apa', 0, 'giraff', 1], void_obj=Void)
        self.assertEqual(element, Void)

    
    def test_should_compare_objects(self):
        structure_1 = [[{'abc': 'bcd', 'cde': [1, 2, {'def': {'efg': 'ghi'}}, 4]}]]
        structure_2 = [[{'abc': 'bcd', 'cde': [1, 2, {'def': {'efg': 'ghi'}}, 4]}]]
        generator = my.traverse(structure_1, type_filter=1)
        index, item_1 = next(generator)
        item_2 = my.get_element(structure_2, index)
        self.assertEqual(1, item_1)
        self.assertEqual(item_1, item_2)
        index, item_1 = next(generator)
        item_2 = my.get_element(structure_2, index)
        self.assertEqual(2, item_1)
        self.assertEqual(item_1, item_2)
        index, item_1 = next(generator)
        item_2 = my.get_element(structure_2, index)
        self.assertEqual('ghi', item_1)
        self.assertEqual(item_1, item_2)
        index, item_1 = next(generator)
        item_2 = my.get_element(structure_2, index)
        self.assertEqual(4, item_1)
        self.assertEqual(item_1, item_2)
        index, item_1 = next(generator)
        item_2 = my.get_element(structure_2, index)
        self.assertEquals('bcd', item_1)
        self.assertEqual(item_1, item_2)
        with self.assertRaises(StopIteration) as cm:
            next(generator)

        

if __name__ == '__main__':
    unittest.main()

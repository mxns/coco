import unittest
import my

class MyTests(unittest.TestCase):


    def test_should_traverse_lists(self):
        structure = [[{'abc': 'bcd', 'cde': [1, 2, {'def': {'efg': 'ghi'}}, 4]}]]
        generator = my.traverse(structure, filter=4)
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
        generator = my.traverse(structure, filter=2)
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

        

if __name__ == '__main__':
    unittest.main()
